import {Injectable} from '@angular/core';
import {BackendService} from "./backend.service";
import {Project} from "./project";
import path from 'path'
import slash from 'slash';
import {map} from "rxjs";
import {TreeNode} from "primeng/api";

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  project: Project = {open: false, rootFolder: '', projectName: ''};
  listeners: Function[] = [];


  constructor(private backendService: BackendService) {
  }

  openProject(rootFolder: string) {
    this.project.open = true;
    this.project.rootFolder = slash(rootFolder);
    this.callListener('openProject', {open: this.project.rootFolder, rootFolder: this.project.rootFolder});
  }

  createProject(rootFolder: string, projectName: string) {
    this.backendService.createProject(rootFolder, projectName).subscribe(() => {
      this.openProject(rootFolder);
    });
  }

  startProject() {
    this.backendService.startProject(this.project.rootFolder).subscribe({
      next: data => console.log(data),
      error: err => console.log(err)
    });
  }

  /*formatFile(filepath: string) {
    this.backendService.format(filepath).subscribe({
      next: data => console.log(data),
      error: err => console.log(err)
    });
  }*/

  addListener(listener: (event: string, project: Project) => void) {
    this.listeners.push(listener);
  }

  callListener(event: string, data: any) {
    for (const listener of this.listeners)
      listener(event, data);
  }

  getTreeNodes(folderPath: string) {
    return this.backendService.getChildren(folderPath)
      .pipe(map(children => this.toTreeNodes(children as any[])))
  }

  toTreeNodes(nodes: any[]): TreeNode[] {
    let result : TreeNode[] = [];
    for (const node of nodes) {
      if(node.type === "FOLDER")
        result.push({
          label: path.basename(slash(node.path)),
          expandedIcon: 'pi pi-folder-open',
          collapsedIcon: 'pi pi-folder',
          leaf: false,
          data: {path: slash(node.path), type: "FOLDER"},
        })
    }
    for (const node of nodes) {
      if (node.type === "FILE")
        result.push({
          label: path.basename(slash(node.path)),
          icon: 'pi pi-file',
          data: {path: slash(node.path), type: "FILE"},
        })
    }
    return result;
  }
}
