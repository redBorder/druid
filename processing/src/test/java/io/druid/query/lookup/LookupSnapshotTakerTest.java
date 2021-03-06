/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.druid.query.lookup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.metamx.common.ISE;
import com.metamx.common.StringUtils;
import io.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class LookupSnapshotTakerTest
{
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private final ObjectMapper mapper = TestHelper.JSON_MAPPER;


  private LookupSnapshotTaker lookupSnapshotTaker;
  private String basePersistDirectory;

  @Before
  public void setUp() throws IOException
  {
    basePersistDirectory = temporaryFolder.newFolder().getAbsolutePath();
    lookupSnapshotTaker = new LookupSnapshotTaker(mapper, basePersistDirectory);
    mapper.registerSubtypes(LookupFactoryMock.class);
    mapper.registerSubtypes(LookupExtractorFactory.class);
    mapper.addMixIn(LookupExtractorFactory.class, LookupFactoryMock.class);
  }

  @Test
  public void testTakeSnapshotAndPullExisting() throws IOException
  {

    LookupBean lookupBean = new LookupBean();
    lookupBean.name = "name";
    lookupBean.factory = new LookupFactoryMock();
    List<LookupBean> lookupBeanList = Lists.newArrayList(lookupBean);
    lookupSnapshotTaker.takeSnapshot(lookupBeanList);
    List<LookupBean> actualList = lookupSnapshotTaker.pullExistingSnapshot();
    Assert.assertEquals(lookupBeanList, actualList);
  }

  @Test(expected = ISE.class)
  public void testIOExceptionDuringLookupPersist() throws IOException
  {
    File directory = temporaryFolder.newFolder();
    directory.setReadOnly();
    LookupSnapshotTaker lookupSnapshotTaker = new LookupSnapshotTaker(mapper, directory.getAbsolutePath());
    LookupBean lookupBean = new LookupBean();
    lookupBean.name = "name";
    lookupBean.factory = new LookupFactoryMock();
    List<LookupBean> lookupBeanList = Lists.newArrayList(lookupBean);
    lookupSnapshotTaker.takeSnapshot(lookupBeanList);
  }


  @Test
  public void tesLookupPullingFromEmptyFile() throws IOException
  {
    File snapshotFile = lookupSnapshotTaker.getPersistFile();
    Assert.assertTrue(snapshotFile.createNewFile());
    Assert.assertEquals(Collections.EMPTY_LIST, lookupSnapshotTaker.pullExistingSnapshot());
  }

  @Test(expected = ISE.class)
  public void tesLookupPullingFromCorruptFile() throws IOException
  {
    File snapshotFile = lookupSnapshotTaker.getPersistFile();
    Assert.assertTrue(snapshotFile.createNewFile());
    byte[] bytes = StringUtils.toUtf8("test corrupt file");
    Files.write(bytes, snapshotFile);
    lookupSnapshotTaker.pullExistingSnapshot();
  }

  @Test
  public void testLookupPullingFromNonExistingFile() throws IOException
  {
    File directory = temporaryFolder.newFolder();
    LookupSnapshotTaker lookupSnapshotTaker = new LookupSnapshotTaker(mapper, directory.getAbsolutePath());
    List<LookupBean> actualList = lookupSnapshotTaker.pullExistingSnapshot();
    Assert.assertEquals(Collections.EMPTY_LIST, actualList);
  }

  @JsonTypeName("mock")
  private class LookupFactoryMock implements LookupExtractorFactory
  {

    @JsonCreator
    public LookupFactoryMock()
    {
    }

    @Override
    public boolean start()
    {
      return true;
    }

    @Override
    public boolean close()
    {
      return true;
    }

    @Override
    public boolean replaces(@Nullable LookupExtractorFactory other)
    {
      return false;
    }

    @Override
    public LookupExtractor get()
    {
      return null;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof LookupFactoryMock;
    }
  }
}
