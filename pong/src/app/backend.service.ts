import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class BackendService {

  constructor(private http: HttpClient) {
  }

  rootURL: string = 'http://localhost:4299';

  createProject(path: string, name: string) {
    return this.http.post(this.rootURL + '/newProject', {path: path, name: name});
  }

  startProject(path: string) {
    return this.http.post(this.rootURL + '/startProject', {path: path});
  }

  getFile(path: string) {
    return this.http.post(this.rootURL + '/file', {path: path}, {responseType: "text"});
  }

  createFile(path: string) {
    return this.http.post(this.rootURL + '/newFile', {path: path});
  }
  writeFile(path: string, content: string) {
    return this.http.post(this.rootURL + '/writeFile', {path: path, content: content});
  }

  deleteFile(path: string) {
    return this.http.post(this.rootURL + '/deleteFile', {path: path});
  }

  createFolder(path: string) {
    return this.http.post(this.rootURL + '/newFolder', {path: path});
  }

  checkDotNetPresence() {
    return this.http.get(this.rootURL + '/checkDotNet');
  }

  getChildren(path: string) {
    return this.http.post(this.rootURL + '/getChildren', {path: path});
  }

  format(path: string) {
    return this.http.post(this.rootURL + '/format', {path: path},{responseType: "text"});
  }

}
