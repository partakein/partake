//
// partake-util.js is a collection of utility methods.
//

(function() {
    function _enclosingForm(elem) {
        while (elem) {
            if (elem.tagName == "FORM")
                return elem;

            elem = elem.parentNode;
        }

        return null;
    }

    $.fn.extend({
        // Returns the enclosing form if any.
        form: function() {
            return this.map(function() {
                return _enclosingForm(this);
            });
        }
    });
})();

