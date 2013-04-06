#
# Cookbook Name:: init-db
# Recipe:: default
#
include_recipe "build-essential"
include_recipe "postgresql::server"
include_recipe "database"

gem_package "pg" do
  action :install
end

postgresql_connection_info = {:host => "localhost",
                              :port => 5432,
                              :username => 'postgres',
                              :password => node['postgresql']['password']['postgres']}

postgresql_database_user 'partake' do
  connection postgresql_connection_info
  password "partake"
  action :create
end

%w{partake-dev partake-test}.each do |db_name|
  postgresql_database db_name do
    connection postgresql_connection_info
    action :create
  end

  postgresql_database_user 'partake' do
    connection postgresql_connection_info
    database_name db_name
    privileges [:all]
    action :grant
  end
end

