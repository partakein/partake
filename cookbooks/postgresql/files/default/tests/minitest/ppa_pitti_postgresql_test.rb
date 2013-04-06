#
# Copyright 2012, Opscode, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

require File.expand_path('../support/helpers', __FILE__)

describe 'postgresql::ppa_pitti_postgresql' do
  include Helpers::Postgresql

  it 'creates the Pitti PPA sources.list' do
    skip unless %w{debian}.include?(node['platform_family'])
    file("/etc/apt/sources.list.d/pitti-postgresql-ppa.list").must_exist
  end

  it 'installs postgresql-client-9.2' do
    package("postgresql-client-9.2").must_be_installed
  end

  it 'makes psql version 9.2 available' do
    psql = shell_out("psql --version")
    assert psql.stdout.include?("psql (PostgreSQL) 9.2")
  end
end
