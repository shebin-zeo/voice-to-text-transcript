import { CommonModule } from '@angular/common';
import { Component, inject, InjectionToken, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranscriptionService } from '../../../core/services/transcription.service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';


interface HistoryItem {
  id: string;
  fileName: string;
  fileSize: number;
  malayalamText: string;
  englishText: string;
  status: string;
}

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule,ToastModule],
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss',
  providers:[MessageService]
})
export class HistoryComponent implements OnInit {
  historyList: HistoryItem[] = [];

  


  private messageService=inject(MessageService);


  private historyService=inject(TranscriptionService)

  ngOnInit(): void {
    this.loadData();
  }

 
  loadData(): void {


    this.historyService.chatsHistory().subscribe({
      next:(res)=>{

        this.historyList=res;

        console.log(res)
      },error:(err)=>{
        console.log("Have issue with backend : ",err.error)
      }
    })
  }

  formatFileSize(bytes: number): string {

  if (!bytes || bytes === 0) {
    return '0 MB';
  }

  const mb = bytes / (1024 * 1024);

  return `${mb.toFixed(2)} MB`;
}



selectedHistory: any = null;

openDetails(item: any): void {
  this.selectedHistory = item;
}

closeDetails(): void {
  this.selectedHistory = null;
}


copyText(text:string):void{

this.messageService.add({
  severity:'success',
  summary:'Text Copied',
  detail:'Transcription copied successfully'

})

   navigator.clipboard.writeText(text);
}

}