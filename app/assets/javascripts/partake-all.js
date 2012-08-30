// TODO: Due to the bug of Play framework, we cannot simply copy the bootstrap
// javascript files. We cannot have any extra javascript files in bootstrap folder.
require('bootstrap/bootstrap.js');

require('jquery/plugins/jquery-ui-timepicker-addon.js');
require('jquery/plugins/jquery-ui-timepicker-ja.js');
require('jquery/plugins/jquery-ui-datepicker-ja.js');
require('jquery/plugins/jquery-fileupload.js');
require('jquery/plugins/jquery-iframe-transport.js');
require('jquery/plugins/jquery-json.js');
require('jquery/plugins/jquery-masonry.js');
require('jquery/plugins/jquery-ui-widget.js');

require('partake/partake-util.js');
require('partake/partake.js');
require('partake/partake-ui.js');
require('partake/jquery-fixup.js');

createPartakeClient = require('partake/partake.js').createPartakeClient;
createPaktakeUIClient = require('partake/partake-ui.js').createPartakeUIClient;
