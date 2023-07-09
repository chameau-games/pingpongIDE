import {
  Component, EventEmitter, Output,
  QueryList,
  ViewChildren
} from '@angular/core';
import path from "path";
import {BackendService} from "../backend.service";
import {EditorComponent} from "../editor/editor.component";

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.css'],
})
export class TabsComponent {
  files: string[] = [];
  selected: number = 0;
  @ViewChildren('editors') editors!: QueryList<EditorComponent>;
  @Output() csharp = new EventEmitter<boolean>();

  getBasename(file: string) {
    return path.basename(file);
  }

  getEditor(filePath: string) {
    return this.editors.find(e => e.filePath === filePath);
  }

  selectTab(filePath: string) {
    setTimeout(() => {
      this.selected = this.files.indexOf(filePath);
      this.changeTab();
    }, 10);
  }

  close(filePath: string) {
    const editor = this.getEditor(filePath);
    if (!editor)
      return;
    const fileContent = editor.code;
    this.backendService.writeFile(filePath, fileContent).subscribe();
    this.removeItem(this.files, filePath);
    this.changeTab();
  }

  constructor(private backendService: BackendService) {
  }

  addTab(filePath: string) {
    if (!this.files.includes(filePath))
      this.files.push(filePath)
  }

  closeAll() {
    for (const filePath of this.files) {
      const editor = this.getEditor(filePath);
      if (!editor)
        return;
      const fileContent = editor.code;
      this.backendService.writeFile(filePath, fileContent).subscribe();
    }
    this.files = [];
  }

  removeItem(arr: any[], value: any) {
    const index = arr.indexOf(value);
    if (index > -1) {
      arr.splice(index, 1);
    }
    return arr;
  }

  closeTab(path: string) {
    this.removeItem(this.files, path);
  }

  formatCurrent(){
    const path = this.files[this.selected];
    const editor = this.getEditor(path);
    this.backendService.format(path).subscribe(text => {
      editor!.code = text;
    });
  }

  changeTab() {
    if (this.files.length === 0)
      this.csharp.emit(false);
    else {
      const filePath = this.files[this.selected];
      this.csharp.emit(path.extname(filePath) === '.cs');
    }
  }
}
