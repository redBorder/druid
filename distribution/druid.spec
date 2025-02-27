
%define __jar_repack 0
Name: druid
Version: 31.0.1
Release: 1
Summary: distribution
License: Apache License, Version 2.0
Vendor: The Apache Software Foundation
URL: https://www.apache.org/
Group: io/druid
Packager: druid.io
Provides: druid
Requires: jre >= 1.7
autoprov: yes
autoreq: yes
BuildArch: noarch
BuildRoot: /home/malvads/duird/druid-druid-31.0.1/distribution/target/rpm/druid/buildroot

%description
distribution

%install

if [ -d $RPM_BUILD_ROOT ];
then
  mv /home/malvads/duird/druid-druid-31.0.1/distribution/target/rpm/druid/tmp-buildroot/* $RPM_BUILD_ROOT
else
  mv /home/malvads/duird/druid-druid-31.0.1/distribution/target/rpm/druid/tmp-buildroot $RPM_BUILD_ROOT
fi

%files

%attr(-,druid,druid) "/usr/lib/druid"

%pre
#
# Licensed to Metamarkets Group Inc. (Metamarkets) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. Metamarkets licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

# Pre installation script
getent group druid >/dev/null || groupadd -r druid
getent passwd druid >/dev/null || useradd -r -g druid -d /usr/lib/druid -s /sbin/nologin -c "Druid" druid

%post
#
# Licensed to Metamarkets Group Inc. (Metamarkets) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. Metamarkets licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

# Post installation script
install --directory --mode=755 /var/lib/druid
install --directory --mode=755 /var/log/druid
chown -R druid:druid /var/lib/druid
chown -R druid:druid /var/log/druid
