const {app, BrowserWindow, ipcMain, dialog} = require('electron');
const path = require('path');
const electronLocalshortcut = require('electron-localshortcut');
const {spawn} = require("child_process");
const process = require("node:process");
const {get} = require("http");

// Handle creating/removing shortcuts on Windows when installing/uninstalling.
if (require('electron-squirrel-startup')) {
  app.quit();
}

spawn('java', ['-jar', '../quarkus-app/quarkus-run.jar']);

const createWindow = () => {
  // Create the browser window.
  const mainWindow = new BrowserWindow({
    width: 1000,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
    },
    icon: __dirname + '/favicon.ico'
  });

  // and load the index.html of the app.
  if (app.isPackaged)
    mainWindow.loadFile('dist/pong/index.html');
  else {
    mainWindow.loadURL('http://localhost:4200');
    // Open the DevTools.
    electronLocalshortcut.register(mainWindow, 'Shift+CommandOrControl+I', () => {
      mainWindow.webContents.openDevTools();
    });
  }

  mainWindow.removeMenu();

  // close window when closeCommand event fire
  ipcMain.on('close-window', () => mainWindow.close());
  // open folder to create project
  ipcMain.handle('open-folder', (e, title) => dialog.showOpenDialog({
    title: title,
    defaultPath: app.getPath('home'),
    properties: ['openDirectory']
  }));
};

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow);

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
  // shutdown quarkus
  get('http://localhost:4299/quit');
});

app.on('activate', () => {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and import them here.
