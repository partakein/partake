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
    source "http://download.playframework.org/releases/play-#{node[:play][:version]}.zip"
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

bash "eclipsify" do
    user "vagrant"
    group "vagrant"
    cwd "/vagrant"
    code <<-EOH
    rm -rf /home/vagrant/.ivy2
    ln -s /vagrant/.ivy2 /home/vagrant/.ivy2
    #{node[:play][:install_dir]}/play-#{node[:play][:version]}/play eclipsify
    mv .classpath /tmp/.classpath
    cat /tmp/.classpath | sed -e 's"/home/vagrant/.ivy2"#{node[:play][:host_ivy_dir]}"' > .classpath
    EOH
    creates "/vagrant/.project"
end


