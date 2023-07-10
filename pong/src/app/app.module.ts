import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { ExplorerComponent } from './explorer/explorer.component';
import { TabsComponent } from './tabs/tabs.component';
import { EditorComponent } from './editor/editor.component';
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MenubarModule } from "primeng/menubar";
import { ButtonModule } from "primeng/button";
import { SplitterModule } from "primeng/splitter";
import { TabViewModule } from "primeng/tabview";
import { HttpClientModule } from "@angular/common/http";
import { TreeModule } from "primeng/tree";
import { ContextMenuModule } from "primeng/contextmenu";
import { DialogModule } from 'primeng/dialog';
import {ConfirmationService, MessageService} from "primeng/api";
import { ToastModule } from "primeng/toast";
import {MonacoEditorModule} from "ngx-monaco-editor-v2";
import {FormsModule} from "@angular/forms";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {InputTextModule} from "primeng/inputtext";
import {ScrollPanelModule} from "primeng/scrollpanel";

@NgModule({
  declarations: [
    AppComponent,
    ExplorerComponent,
    EditorComponent,
    TabsComponent,
    HeaderComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MenubarModule,
    ButtonModule,
    SplitterModule,
    TabViewModule,
    TreeModule,
    ContextMenuModule,
    HttpClientModule,
    DialogModule,
    ToastModule,
    HttpClientModule,
    MonacoEditorModule.forRoot(),
    FormsModule,
    ConfirmDialogModule,
    InputTextModule,
    ScrollPanelModule
  ],
  providers: [MessageService, ConfirmationService],
  exports: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
