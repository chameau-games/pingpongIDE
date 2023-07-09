import { BackendService } from './backend.service';
import {Component, OnInit, ViewChild} from '@angular/core';
import {ProjectService} from "./project.service";
import {TabsComponent} from "./tabs/tabs.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'pong';
  dialogVisible: boolean = false;
  projectOpened: boolean = false;

  constructor(private backendService: BackendService, private projectService: ProjectService) {}

  @ViewChild('tabs') tabs!: TabsComponent;
  csharp: boolean = false;

  ngOnInit(): void {
    setTimeout(() => this.backendService.checkDotNetPresence().subscribe(() => {
    }, () => {
      this.dialogVisible = true;
    }), 10000);

    this.projectService.addListener((event: string, data: any) => {
      if (event === 'openProject') {
        this.tabs.closeAll();
        this.projectOpened = data.open;
      } else if (event === 'openFile') {
        this.tabs.addTab(data.filePath);
        this.tabs.selectTab(data.filePath);
      } else if (event === 'quit')
        this.tabs.closeAll();
      else if (event === 'deleteFile')
        this.tabs.closeTab(data.path);
      else if (event === 'format')
        this.tabs.formatCurrent();
    })
  }
}


