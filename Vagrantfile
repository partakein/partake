# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "precise32"
  config.vm.box_url = "http://files.vagrantup.com/precise32.box"

  # From host, you can access PARTAKE via http://192.168.33.10:9000/
  config.vm.network :forwarded_port, guest: 9000, host: 9000
  config.vm.network :private_network, ip: "192.168.33.10"

  config.vm.provision :chef_solo do |chef|
    chef.add_recipe "apt-get-update"
    chef.add_recipe "postgresql::server"
    chef.add_recipe "postgresql::client"
    chef.add_recipe "java"
    chef.add_recipe "play"
    chef.add_recipe "init-db"
    chef.cookbooks_path = ["./cookbooks", "./site-cookbooks"]
    chef.json = {
      :postgresql => {
        :version => "9.1",
        :config => {
          :listen_addresses => "localhost"
        },
        :password => {
          :postgres => "postgres"
        }
      },
      :java => {
        :jdk_version => "7"
      },
      :play => {
        :version => "2.0.4",
        :install_dir => "/home/vagrant"
      }
    }
  end

  config.vm.provider :virtualbox do |vb|
    # Play! needs much memory, so we have to provide 1.5 GiB at least.
    # When you reduce this, please modify -Xmx parameter in /home/vagrant/play-2.0.4/framework/build
    vb.customize ["modifyvm", :id, "--memory", "1536"]

    # see https://github.com/mitchellh/vagrant/issues/516
    vb.customize ["modifyvm", :id, "--nictype1", "Am79C973"]
  end
end
