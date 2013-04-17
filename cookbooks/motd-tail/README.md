Description
===========

Updates motd.tail with Chef Roles

Requirements
============

Needs to be used on a system that utilizes /etc/motd.tail, e.g. Ubuntu.

Attributes
==========

* `node['motd-tail']['additional_text']` - Additional text to add to the end
  of the motd.tail (e.g. unauthorized access banner).

Usage
=====

```json
"run_list": [
    "recipe[motd-tail]"
]
```

default
----

Updates motd.tail with useful node data

Examples
--------

For example,

    % ssh myserver.int.example.org
    ***
    Chef-Client - myserver.int.example.org
    ubuntu
    samba_server
    netatalk_server
    munin_server
    rsyslog_server
    ***

    Additional text here when `node['motd-tail']['additional_text']` present.

Testing
=====

This cookbook is using [ChefSpec](https://github.com/acrmp/chefspec) for testing.

    % cd $repo
    % bundle
    % librarian-chef install
    % ln -s ../ cookbooks/motd-tail
    % rspec cookbooks/motd-tail

License and Author
==================

Author:: Nathan Haneysmith <nathan@opscode.com>

Copyright:: 2009-2012, Opscode, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
