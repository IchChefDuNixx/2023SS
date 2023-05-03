// background.js

/* ping pong test */

// On startup, connect to the "ping_pong" app.
let port = browser.runtime.connectNative("clairstone");

// On a click on the browser action, send the app a message.
browser.action.onClicked.addListener(() => {
  browser.windows.create({
    type: "detached_panel",
    url: "about:devtools-toolbox?type=extension"
  });
  console.log("Sending:  ping");
  port.postMessage("ping");  
});

// Listen for messages from the app.
port.onMessage.addListener(function(message) {

  if (message != "DONTLOG") {
    // Handle the completion message here
    console.log("Python script completed with message:", message);
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
