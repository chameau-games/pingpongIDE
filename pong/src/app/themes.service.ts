import {Inject, Injectable} from '@angular/core';
import {DOCUMENT} from "@angular/common";
declare var monaco: any;

@Injectable({
  providedIn: 'root'
})
export class ThemesService {
  editors: any[] = [];
  current = 'arya';

  constructor(@Inject(DOCUMENT) private document: Document) { }

  switchTheme(theme: string) {
    let themeLink = this.document.querySelector('#app-theme') as HTMLLinkElement;
    if (themeLink)
      themeLink.href = theme + '.css';
    this.current = theme;
    monaco.editor.setTheme(theme.includes('arya') ? 'vs-dark' : 'vs');

    // for (const editor of this.editors)
    //   editor.setOption('theme', theme.includes('dark') ? 'ayu-mirage' : 'base16-light');
  }

  addEditor(editor: any) {
    this.editors.push(editor);
  }
}
