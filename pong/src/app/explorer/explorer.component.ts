import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmationService, MenuItem, MessageService, TreeNode} from "primeng/api";
import {ProjectService} from "../project.service";

import path from 'path'
import {TreeNodeExpandEvent, TreeNodeSelectEvent} from "primeng/tree";
import {BackendService} from "../backend.service";


@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.css']
})
export class ExplorerComponent implements OnInit {
  files: TreeNode[] = [];
  menu!: MenuItem[];
  loading: boolean = false;
  selectedNode!: TreeNode;
  dialogValue: string = '';
  @ViewChild('inputTextDialog') inputTextDialog: any;
  newWhat: string = '';

  ngOnInit(): void {
    this.menu = [{
      label: 'Nouveau',
      icon: 'pi pi-plus',
      items: [
        {
          label: 'Fichier',
          icon: 'pi pi-file',
          command: () => this.showInputTextDialog('fichier')
        },
        {
          label: 'Dossier',
          icon: 'pi pi-folder',
          command: () => this.showInputTextDialog('dossier')
        }
      ]
    },
      {
        label: 'Supprimer',
        icon: 'pi pi-trash',
        command: () =>  this.showDeleteDialog()
      }];
    this.projectService.addListener((event: string, data: any) => {
      if (event === 'openProject') {
        this.files = [{
          label: path.basename(data.rootFolder),
          expandedIcon: 'pi pi-folder-open',
          collapsedIcon: 'pi pi-folder',
          leaf: false,
          data: {path: data.rootFolder, type: "FOLDER"}
        }];
        this.projectService.getTreeNodes(data.rootFolder)
          .subscribe(nodes => {
            this.files[0].children = nodes
            this.files[0].expanded = true
          });
      }

    })
  }

  constructor(private projectService: ProjectService, private backendService: BackendService,
              private confirmationService: ConfirmationService, private messageService: MessageService) {
  }

  nodeExpand(event: TreeNodeExpandEvent) {
    if (event.node)
      this.loadChildren(event.node)
  }

  loadChildren(node: any) {
    this.loading = true;
    this.projectService.getTreeNodes(node.data.path).subscribe(nodes => {
      node.children = nodes;
      this.loading = false;
    })
  }

  nodeSelect(event: TreeNodeSelectEvent) {
    this.selectedNode = event.node;
  }

  nodeDoubleClick() {
    if (this.selectedNode.data.type === "FILE")
      this.projectService.callListener('openFile', {filePath: this.selectedNode.data.path})
    else {
      this.selectedNode.expanded = !this.selectedNode.expanded;
      if (this.selectedNode.expanded)
        this.loadChildren(this.selectedNode);
    }
  }

  showInputTextDialog(newWhat: string) {
    this.newWhat = newWhat;
    this.confirmationService.confirm({
      key: 'inputText',
      accept: () => newWhat === 'fichier' ? this.newFile() : this.newFolder(),
    });
    setTimeout(() => this.inputTextDialog.el.nativeElement.querySelector('input').focus(), 50);
  }

  newFolder() {
    const parentPath = this.selectedNode.data.type === 'FOLDER' ? this.selectedNode.data.path
      : path.dirname(this.selectedNode.data.path);
    this.backendService.createFolder(path.join(parentPath, this.dialogValue))
      .subscribe(() => {
        this.dialogValue = '';
        const target = this.selectedNode.data.type === 'FOLDER'
          ? this.selectedNode : this.selectedNode.parent;
        this.loadChildren(target);
        target!.expanded = true;
      });
  }

  newFile() {
    const parentPath = this.selectedNode.data.type === 'FOLDER' ? this.selectedNode.data.path
      : path.dirname(this.selectedNode.data.path);
    const newFilePath = path.join(parentPath, this.dialogValue);
    this.backendService.createFile(newFilePath)
      .subscribe(() => {
        this.dialogValue = '';
        const target = this.selectedNode.data.type === 'FOLDER'
          ? this.selectedNode : this.selectedNode.parent;
        this.loadChildren(target);
        target!.expanded = true;
        this.projectService.callListener('openFile', {filePath: newFilePath})
      });
  }

  deleteFile() {
    this.projectService.callListener('deleteFile', {path: this.selectedNode.data.path});
    this.backendService.deleteFile(this.selectedNode.data.path).subscribe(() => {
      this.loadChildren(this.selectedNode.parent);
    });
  }

  showDeleteDialog() {
    if (this.selectedNode.parent !== undefined)
      this.confirmationService.confirm({
        key: 'delete',
        accept: () => this.deleteFile(),
      });
    else
      this.messageService.add({ severity: 'error', summary: 'Erreur',
        detail: 'Impossible de supprimer le dossier racine du projet.' });
  }
}
