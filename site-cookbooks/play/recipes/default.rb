# default.rb
#

package "zip"

directory '/vagrant/.ivy2' do
  owner "vagrant"
  group "vagrant"
  mode  "0755"
  action :create
end

remote_file "#{node[:play][:install_dir]}/play-#{node[:play][:version]}.zip" do
    user "vagrant"
    source "http://downloads.typesafe.com/play/#{node[:play][:version]}/play-#{node[:play][:version]}.zip"
    mode "0644"
    notifies :run, "execute[install-play]", :immediately
    action :create_if_missing
end

execute "install-play" do
    user "vagrant"
    cwd node['play']['install_dir']
    command <<-EOH
    unzip play-#{node[:play][:version]}.zip
    EOH
    action :nothing
end

# remove ~/.ivy2 if it exists to replace with symbolic link
directory "/home/vagrant/.ivy2" do
    recursive true
    action :delete
end

# to share libraries, ~/.ivy2 should be a link to /vagrant/.ivy2
link "/home/vagrant/.ivy2" do
    owner "vagrant"
    group "vagrant"
    to "/vagrant/.ivy2"
    action :create
end

template "/home/vagrant/eclipsify.sh" do
    owner "vagrant"
    group "vagrant"
    mode  0700
end

bash "eclipsify" do
    user "vagrant"
    group "vagrant"
    cwd "/vagrant"
    code <<-EOH
    /home/vagrant/eclipsify.sh
    EOH
    creates "/vagrant/.project"
end


