#
# Cookbook Name:: apt-get-update
# Recipe:: default
#
# http://stackoverflow.com/questions/9246786
#

bash "apt-get update" do
  code <<-EOC
    sudo apt-get update
  EOC
end
