// background.js

/* ping pong test */

// On startup, connect to the "ping_pong" app.
let port = browser.runtime.connectNative("clairstone");

// On a click on the browser action, send the app a message.
browser.action.onClicked.addListener(() => {
  // TODO: make the button useful
  browser.windows.create({
    type: "detached_panel",
    url: "about:devtools-toolbox?type=extension"
  });
  console.log("Sending: ping");
  port.postMessage({data:"ping"});  
});

// Listen for messages from the app.
port.onMessage.addListener(function(message) {

  if (message.data != "DONTLOG") {
    // Handle the completion message here
    console.log("Python script completed with message:", message.data);
  }

  if (message.toHash) {
    // hash_encode({commander:{id:431,level:6,runes:[]}, deck:[{id:26133,level:6,runes:[]}]})
    if (message.toHash) {
      const numsims = 10000;
      const bges = "164,165";
      const use_tower = true;

      let deck1;
      fetch('http://localhost:3000?hashable=' + JSON.stringify(message.toHash.player), {mode: 'cors'})
      .then(response => response.text())
      .then(result => {
        deck1 = result;
        return fetch('http://localhost:3000?hashable=' + JSON.stringify(message.toHash.opponent), {mode: 'cors'})
      })
      .then(response => response.text())
      .then(result => {
        const deck2 = result;
        return fetch(`http://localhost:3001?deck1=${deck1}&deck2=${deck2}&use_tower=${use_tower}&bges=${bges}&numsims=${numsims}`);
      })
      .then(response => response.text())
      .then(result => {
        console.log(result);
      })
      .catch(error => {
        console.error("Error:", error);
      })
    }
  }

  // show(message);

});

// Handle errors
port.onDisconnect.addListener(function() {
  if (browser.runtime.lastError) {
    console.error("Disconnected due to an error:", browser.runtime.lastError);
  } else {
    console.log("Port disconnected.");
  }
});


/* native app & network stuff */

function listener(details) {

  let filter = browser.webRequest.filterResponseData(details.requestId);
  let decoder = new TextDecoder("utf-8");
  let encoder = new TextEncoder();
  let responseDataChunks = [];

  // intercept any response that matches the urls
  filter.ondata = (event) => {
    let str = decoder.decode(event.data, {stream: false});
    responseDataChunks.push(str);
    // console.log('ondata event: ', str.length); // Add a log statement to check the ondata event
  }

  // and pass them to the server
  filter.onstop = (event) => {
    let responseData = responseDataChunks.join("");
    // console.log('onstop event: ', responseData.length, "(", responseDataChunks.length, "chunk(s) )"); // Add a log statement to check the onstop event
    port.postMessage({"url": details.url, "data": responseData})
    filter.write(encoder.encode(responseData));
    filter.disconnect();
  }

  return {};
}

browser.webRequest.onBeforeRequest.addListener(
  listener,
  {urls: ["*://*.synapse-games.com/*"]},
  ["blocking"]
)

function show(message) {
  browser.tabs.query({active: true, currentWindow: true}).then((tabs) => {
    browser.tabs.sendMessage(tabs[0].id, {type: "console", content: message});
  });
}
