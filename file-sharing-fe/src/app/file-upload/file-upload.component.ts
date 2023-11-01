import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {
  fileInfo: any = null;
  uploadedFileInfo: any = null;
  shareLink: string = '';
  expireTime: Date | null = null;
  fileToUpload: File | null = null;

  isLoading: boolean = false;
  isUploading: boolean = false;

  constructor(private http: HttpClient) { }

  onFileSelected(event: any): void {
    this.fileToUpload = event.target.files[0] as File;
    this.fileInfo = {
      name: this.fileToUpload.name,
      size: this.fileToUpload.size
    };
  }

  uploadFile(): void {
    this.isUploading = true;
    if (this.fileToUpload) {
      const formData: FormData = new FormData();
      formData.append('file', this.fileToUpload, this.fileToUpload.name);
      const url = `${environment.apiUrl}/api/files/upload`;

      this.http.post<any>(url, formData).subscribe(response => {
        console.log('File uploaded successfully', response);
        this.uploadedFileInfo = response;
        this.isUploading = false;
      }, error => {
        console.error('Error uploading file', error);
        this.isUploading = false;
      });
    }
  }

  generateLink(): void {
    this.isLoading = true;
    const payload = {"fileId": this.uploadedFileInfo.id};
    const url = `${environment.apiUrl}/api/links/generate`;

    this.http.post<any>(url, payload).subscribe(response => {
      this.shareLink = response.sharingLink;
      this.expireTime = new Date(response.expireTime);
      console.log('Link generated successfully', response);
      this.isLoading = false;
    }, error => {
      console.error('Error generating link', error);
      this.isLoading = false;
    });
  }
}
