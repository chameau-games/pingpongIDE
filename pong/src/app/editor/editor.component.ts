import {
  Component,
  ElementRef,
  Input,
  OnChanges, OnDestroy,
  SimpleChanges
} from '@angular/core';
import {ThemesService} from "../themes.service";
import {BackendService} from "../backend.service";
import path from "path";

const MODES : any = {'.cs': 'csharp', '.csproj': 'xml', '.xml': 'xml', '.json': 'json'}

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnChanges, OnDestroy {
  @Input() filePath : string = '';
  saveInterval!: NodeJS.Timer;
  editorOptions :any = {
    theme: this.themesService.current.includes('arya') ? 'vs-dark' : 'vs',
    minimap: {enabled: false},
    automaticLayout: true
  };
  code: string= '';
  version = 0;


  constructor(private hostEl: ElementRef, private themesService: ThemesService, private backendService: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if ('filePath' in changes) {
      this.editorOptions.language = MODES[path.extname(this.filePath).toLowerCase()];
      this.backendService.getFile(this.filePath).subscribe(data => {
        this.code = data;
      });
    }
  }

  onInit(editor: any) {
    clearInterval(this.saveInterval);
    this.saveInterval = setInterval(() => {
      if (editor.getModel().getVersionId() === this.version)
        return;
      this.version = editor.getModel().getVersionId();
      this.backendService.writeFile(this.filePath, this.code).subscribe();
    }, 2000);

  }

  ngOnDestroy(): void {
    clearInterval(this.saveInterval);
  }
}
