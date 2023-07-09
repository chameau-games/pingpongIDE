import {Component, Input, OnInit} from '@angular/core';
import {MenuItem} from "primeng/api";
import {ThemesService} from "../themes.service";
import {ProjectService} from "../project.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit{
  items: MenuItem[] | undefined;
  @Input() projectOpened: boolean = false;
  @Input() csharp: boolean = false;

  ngOnInit(): void {
    this.items = [
      {
        label: 'Fichier',
        icon: 'pi pi-fw pi-file',
        items: [
          {
            label: 'Nouveau projet',
            icon: 'pi pi-fw pi-plus',
            command: async () => {
              // @ts-ignore
              const result = await window.electronAPI.openFolder('Choisissez un dossier dans lequel créer le projet');
              const rootFolder = result.filePaths[0];
              const projectName = rootFolder.replace(/^.*[\\\/]/, '');
              this.projectService.createProject(rootFolder, projectName);
            }
          },
          {
            label: 'Ouvrir...',
            icon: 'pi pi-fw pi-folder-open',
            command: async () => {
              // @ts-ignore
              const result = await window.electronAPI.openFolder('Choisissez le dossier racine du projet');
              const rootFolder = result.filePaths[0];
              this.projectService.openProject(rootFolder);
            }
          },
          {
            separator: true
          },
          {
            label: 'Quitter',
            icon: 'pi pi-fw pi-sign-out',
            command: () => {
              this.projectService.callListener('quit', null);
              // @ts-ignore
              window.electronAPI.closeWindow()
            }
          }
        ]
      },
      {
        label: 'Thèmes',
        icon: 'pi pi-fw pi-palette',
        items: [
          {
            label: 'Clair',
            icon: 'pi pi-fw pi-sun',
            command: () => this.themesService.switchTheme('saga-orange')
          },
          {
            label: 'Sombre',
            icon: 'pi pi-fw pi-moon',
            command: () => this.themesService.switchTheme('arya-orange')
          }
        ]
      }
    ];
  }

  constructor(private themesService: ThemesService, private projectService: ProjectService) {}

  start() {
    this.projectService.startProject();
  }

  format() {
    this.projectService.callListener("format",{});
   }
}
