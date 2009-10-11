if (typeof AJS == "undefined" && typeof YAHOO != "undefined") {
    var AJS = (function () {
        var bindings = {click: {}},
            initFunctions = [],
            included = [],
            isInitialised = false;

        var userAgent = navigator.userAgent.toLowerCase();
        var usingJWebUnit = /jwebunit/.test( userAgent );
        
        return {

            /**
             * Allows JQuery check by other JS classes, independent of browser.
             * Don't initialise jQuery in the tests. It's sloooooowwwww...
             */
            useJQuery: !usingJWebUnit,

            params: {},
            /**
            * Returns an HTMLElement reference.
            * @method $
            * @param {String | HTMLElement |Array} el Accepts a string to use as an ID for getting a DOM reference, an actual DOM reference, or an Array of IDs and/or HTMLElements.
            * @return {HTMLElement | Array} A DOM reference to an HTML element or an array of HTMLElements.
            */
            $: YAHOO.util.Dom.get,
            /**
            * Returns a array of HTMLElements with the given class.
            * For optimized performance, include a root node when possible.
            * @method $$
            * @param {String} className The class name to match against
            * @param {String | HTMLElement} root (optional) The HTMLElement or an ID to use as the starting point 
            * @param {Function} apply (optional) A function to apply to each element when found 
            * @return {Array} An array of elements that have the given class name
            */
            $$: function (className, root, apply) {
                return YAHOO.util.Dom.getElementsByClassName(className, null, root, apply);
            },
            /**
            * Returns a array of HTMLElements that pass the test applied by supplied boolean method.
            * For optimized performance, include a root node when possible.
            * @method $$$
            * @param {Function} method - A boolean method for testing elements which receives the element as its only argument.
            * @param {String | HTMLElement} root (optional) The HTMLElement or an ID to use as the starting point 
            * @param {Function} apply (optional) A function to apply to each element when found 
            * @return {Array} Array of HTMLElements
            */
            $$$: function (method, root, apply) {
                return YAHOO.util.Dom.getElementsBy(method, null, root, apply);
            },

            log: function(obj) {
                if (typeof console != "undefined" && console.log)
                    console.log(obj);
            },

            Event: YAHOO.util.Event,
            Dom: YAHOO.util.Dom,
            stopEvent: function(e) {
                this.Event.stopEvent(e);
                return false; // required for JWebUnit pop-up links to work properly
            },
            include: function (url) {
                if (!this.contains(included, url)) {
                    included.push(url);
                    var s = document.createElement("script");
                    s.src = url;
                    document.getElementsByTagName("body")[0].appendChild(s);
                }
            },
            /**
            * Shortcut function to toggle class name of an element.
            * @method toggleClassName
            * @param {String | HTMLElement} element The HTMLElement or an ID to toggle class name on.
            * @param {String} className The class name to remove or add.
            */
            toggleClassName: function (element, className) {
                if (!(element = this.$(element))) return;
                if (this.Dom.hasClass(element, className))
                    this.Dom.removeClass(element, className);
                else
                    this.Dom.addClass(element, className);
            },
            /**
             * Shortcut function adds or removes "hidden" classname to an element based on a passed boolean.
             * @method setVisible
             * @param {String | HTMLElement} element The HTMLElement or an ID to show or hide.
             * @param {boolean} show true to show, false to hide
             */
            setVisible: function (element, show) {
                if (!(element = this.$(element))) return;
                var isHidden = this.Dom.hasClass(element, "hidden");
                if (isHidden && show) {
                    this.Dom.removeClass(element, "hidden");
                }
                else if (!isHidden && !show) {
                    this.Dom.addClass(element, "hidden");
                }
            },
            /**
             * Shortcut function to see if passed element is currently visible on screen.
             * @method isVisible
             * @param {String | HTMLElement} element The HTMLElement or an ID to check.
             */
            isVisible: function (element) {
                return !this.Dom.hasClass(element, "hidden");
            },
            /**
            * Runs functions from list (@see toInit) and attach binded funtions (@see bind)
            * @method init
            */
            init: function (){
                var ajs = this;
                this.$$("parameters", null, function(el) {
                    var inputs = el.getElementsByTagName("input");
                    for (var i = 0, ii = inputs.length; i < ii; i++) {
                        var value = inputs[i].value;
                        ajs.params[inputs[i].id] = (value.match(/^(tru|fals)e$/i)?value.toLowerCase() == "true":value);
                    }
                });
                isInitialised = true;
                AJS.initFunctions = initFunctions;
                for (var i = 0, ii = initFunctions.length; i < ii; i++) {
                    if (typeof initFunctions[i] == "function") {
                        initFunctions[i]();
                    }
                }
                var all = document.getElementsByTagName("*");
                for (handler in bindings) {
                    for (className in bindings[handler]) {
                        if (className.charAt(0) == "#") {
                            for (var j = 0, jj = bindings[handler][className].length; j < jj; j++) {
                                this.Event.addListener(this.$(className.substring(1)), handler, bindings[handler][className][j]);
                            }
                            delete bindings[handler][className];
                        } else {
                            var ajs = this;
                            for (var j = 0, jj = bindings[handler][className].length; j < jj; j++) {
                                this.$$(className, null, function (el) {
                                    ajs.Event.addListener(el, handler, bindings[handler][className][j]);
                                });
                            }
                            delete bindings[handler][className];
                        }
                    }
                }
            },
            /**
            * Adds functions to the list of methods to be run on initialisation.
            * @method toInit
            * @param {Function} func Function to be call on initialisation.
            * @return AJS object.
            */
            toInit: function (func) {
                initFunctions.push(func);
                return this;
            },
            /**
            * Utility method returns scrolling position.
            * @method getScrollPosition
            * @return object with left and top properties presenting scrolling offset.
            */
            getScrollPosition: function () {
                return {left:this.Dom.getDocumentScrollLeft(), top:this.Dom.getDocumentScrollTop()};
            },
            /**
            * Binds given function to some object or set of objects as event handlers by class name or id.
            * @method bind
            * @param {String} reference Element or name of the element class. Put "#" in the beginning od the string to use it as id.
            * @param {String} handlerName (optional) Name of the event i.e. "click", "mouseover", etc.
            * @param {Function} func Function to be attached.
            * @return AJS object.
            */
            bind: function () {
                if (arguments.length < 2) {
                    throw new Error("Not enough parameters for bind function");
                    return false;
                }
                var reference = arguments[0],
                    handlerName = "click",
                    func = arguments[arguments.length - 1];
                if (arguments.length > 2) {
                    handlerName = arguments[1];
                }
                if (typeof func != "function") {
                    throw new Error("Can not bind non function object");
                    return false;
                }
                var handler = (typeof handlerName == "string")?handlerName.toLowerCase():"click";
                if (isInitialised) {
                    if (typeof reference == "object") {
                        AJS.Event.addListener(reference, handler, func);
                    } else {
                        if (reference.charAt(0) == "#") {
                            AJS.Event.addListener(AJS.$(reference.substring(1)), handler, func);
                        } else {
                            AJS.$$(reference, null, function (el) {
                                AJS.Event.addListener(el, handler, func);
                            });
                        }
                    }
                } else {
                    if (typeof bindings[handler] != "object") {
                        bindings[handler] = {};
                        bindings[handler][reference] = [];
                    }
                    if (typeof bindings[handler][reference] != "object") {
                        bindings[handler][reference] = [func];
                    } else {
                        bindings[handler][reference].push(func);
                    }
                }
                return this;
            },
            /**
            * Finds the index of an element in the array.
            * @method indexOf
            * @param item Array element which will be searched.
            * @param fromIndex (optional) the index from which the item will be searched. Negative values will search from the
            * end of the array.
            * @return a zero based index of the element.
            */
            indexOf: function (array, item, fromIndex) {
                var length = array.length;
                if (fromIndex == null) {
                  fromIndex = 0;
                } else {
                    if (fromIndex < 0) {
                      fromIndex = Math.max(0, length + fromIndex);
                    }
                }
                for (var i = fromIndex; i < length; i++) {
                  if (array[i] === item) return i;
                }
                return -1;
            },

            /**
            * Looks for an element inside the array.
            * @method contains
            * @param item Array element which will be searched.
            * @return {Boolean} Is element in array.
            */
            contains: function (array, item) {
                return this.indexOf(array, item) > -1;
            }
        };
    })();

    AJS.Event.onDOMReady(function () {AJS.init();});
}
