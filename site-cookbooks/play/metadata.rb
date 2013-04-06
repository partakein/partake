# metadata.rb
# Author: Jake Davis (jake.davis5989@gmail.com)
#

maintainer       "Jake Davis"
maintainer_email "jake.davis5989@gmail.com"
license          "MIT"
description      "Installs Play framework"
version          "0.1.1"

# I'll add more!
%w{ ubuntu debian }.each do |os|
    supports os
end
