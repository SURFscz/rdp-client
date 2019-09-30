<!DOCTYPE HTML>
<html>
    <body>
        <script type="text/javascript">
        </script>

        <script type="text/javascript" src="guacamole-common-js/all.min.js"></script>
        <center>
          <b>RDP Session for ${user}</b> using pwd: ${pwd} <input type=text style='margin: 0px;' id="clipboard"></input>
          <div style='margin: 0px;' id="display"></div>
        </center>
        <script type="text/javascript">
            var display = document.getElementById("display");
            var clipboard = document.getElementById("clipboard");

            const range = document.createRange();
	    const selection = document.getSelection();

            var guac = new Guacamole.Client(
                new Guacamole.HTTPTunnel("tunnel")
            );

            var guac_display = guac.getDisplay().getElement();

            display.appendChild(guac_display);
            guac.onerror = function(error) {
                alert(error);
            };

            guac.connect();
            window.onunload = function() {
                guac.disconnect();
            };

            var mouse = new Guacamole.Mouse(guac.getDisplay().getElement());
            mouse.onmousedown = 
            mouse.onmouseup   =
            mouse.onmousemove = function(mouseState) {
                guac.sendMouseState(mouseState);
            };
            
            async function get_clipboard(keysym) {
                clipboard.value = await navigator.clipboard.readText();
                var writer;
                var stream = guac.createClipboardStream('text/plain');
                writer = new Guacamole.StringWriter(stream);
                writer.sendText(clipboard.value);
                writer.sendEnd();
                guac.sendKeyEvent(1, keysym);
            }

            function my_onkeydown(keysym) {
                if (event.ctrlKey && event.keyCode == 86) {
                  //console.log('ctrl+v pressed');
                  get_clipboard(keysym);
                } else {
                  guac.sendKeyEvent(1, keysym);
                }
            };

            function my_onkeyup(keysym) {
                guac.sendKeyEvent(0, keysym);
            };

            var keyboard = new Guacamole.Keyboard(document);
            keyboard.onkeydown = my_onkeydown;
            keyboard.onkeyup = my_onkeyup;

            display.addEventListener('mouseleave', function() {
                //console.log('mouseleave display');
                keyboard.onkeydown = null;
                keyboard.onkeyup = null;
            });

            display.addEventListener('mouseenter', function() {
                //console.log('mouseenter display');
                keyboard.onkeydown = my_onkeydown;
                keyboard.onkeyup = my_onkeyup;
            });
/*
            display.addEventListener('click', function() {
                //console.log('click display');
                keyboard.onkeydown = my_onkeydown;
                keyboard.onkeyup = my_onkeyup;
            });

            clipboard.addEventListener('click', function() {
                //console.log('click clipboard');
		keyboard.onkeydown = null;
		keyboard.onkeyup = null;
	    });
*/

            // Handle any received clipboard data
	    guac.onclipboard = function clientClipboardReceived(stream, mimetype) {
	      var reader;

	      // If the received data is text, read it as a simple string
	      if (/^text\//.exec(mimetype)) {

		reader = new Guacamole.StringReader(stream);

		// Assemble received data into a single string
		var data = '';
		reader.ontext = function textReceived(text) {
		    data += text;
		};

		// Set clipboard contents once stream is finished
		reader.onend = function textComplete() {
                    //console.log('data: ' + data + ',mt: ' + mimetype);
		    clipboard.value = data;
		    clipboard.select();
		    //range.selectNodeContents(clipboard);
		    //selection.addRange(range);
		    document.execCommand('copy');
		    selection.removeAllRanges();
		};
	      }
	    };


         </script>
    </body>
</html>

