import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.development';
import { Observable } from 'rxjs';
import { AudioResponse } from '../model/response.model';

@Injectable({
  providedIn: 'root'
})
export class TranscriptionService {

  constructor(
  private http:HttpClient
  ) { }


  private apiUrl:string=environment.api;


  transcribeAudio(file:File):Observable<any>{

    const formData=new FormData();
    formData.append('file',file)

    return this.http.post(`${this.apiUrl}/api/audio/transcribe`,formData)
  }

  chatsHistory():Observable<any>{
    return this.http.get(`${this.apiUrl}/api/audio/history`)
  }


}
