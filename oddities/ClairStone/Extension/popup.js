let popupWindow;

function openPopupWindow() {
  // Create the popup window with a specific size and position
  popupWindow = window.open("", "", "width=400,height=300,top=100,left=100,resizable=yes");
  
  // Add event listeners to allow the user to move the window
  popupWindow.addEventListener("mousedown", startDragging);
  popupWindow.addEventListener("mouseup", stopDragging);
  
  // Load your popup HTML content into the window
  popupWindow.document.body.innerHTML = "<h1>Hello, world!</h1>";

  // Create a new div element to display the console output
  const consoleOutput = document.createElement("div");
  popupWindow.document.body.appendChild(consoleOutput);

  // Listen for messages from the background script and display them in the console output
  browser.runtime.onMessage.addListener((message) => {
    if (message.type === "console") {
      consoleOutput.innerText += message.content + "\n";
    }
  });

}

function startDragging(event) {
  popupWindow.dragStartX = event.clientX;
  popupWindow.dragStartY = event.clientY;
  popupWindow.addEventListener("mousemove", dragWindow);
}

function dragWindow(event) {
  const deltaX = event.clientX - popupWindow.dragStartX;
  const deltaY = event.clientY - popupWindow.dragStartY;
  popupWindow.moveTo(popupWindow.screenX + deltaX, popupWindow.screenY + deltaY);
}

function stopDragging() {
  popupWindow.removeEventListener("mousemove", dragWindow);
}

document.getElementById("my-button").addEventListener("click", openPopupWindow);

browser.runtime.onMessage.addListener((message) => {
  if (message.type === "console") {
    const consoleDiv = document.getElementById("console");
    consoleDiv.innerHTML += message.content + "<br>";
  }
});