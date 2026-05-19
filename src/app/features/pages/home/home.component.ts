import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranscriptionService } from '../../../core/services/transcription.service';
import { AudioResponse } from '../../../core/model/response.model';
import { RouterLink } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';



@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,RouterLink,ToastModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  providers:[MessageService]
})
export class HomeComponent {


  selectedFileName: string | null = null;

  audioUrl: string | null = null;

  selectedFile!: File;



  malayalamText = '';

  englishText = '';


  isDragging = false;

  isRecording = false;

  isProcessing = false;

  errorMessage = '';


  mediaRecorder!: MediaRecorder;

  audioChunks: Blob[] = [];

  recordedBlob!: Blob;



  constructor(
    private transcriptionService: TranscriptionService,
    private messageService:MessageService
  ) {}



  onDragOver(event: DragEvent) {

    event.preventDefault();

    this.isDragging = true;

  }

  onDragLeave(event: DragEvent) {

    event.preventDefault();

    this.isDragging = false;

  }



  onDrop(event: DragEvent) {

    event.preventDefault();

    this.isDragging = false;

    if (event.dataTransfer?.files?.length) {

      const file =
        event.dataTransfer.files[0];

      this.handleSelectedFile(file);

    }

  }

  

  onFileSelected(event: Event) {

    const input =
      event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {

      const file = input.files[0];

      this.handleSelectedFile(file);

    }

  }

  

  handleSelectedFile(file: File) {

    // RESET OLD RESULTS

    this.malayalamText = '';

    this.englishText = '';

    this.errorMessage = '';

    this.selectedFile = file;

    this.selectedFileName = file.name;

    // this.audioUrl =
    //   URL.createObjectURL(file);

    // Create NEW preview
  this.audioUrl = URL.createObjectURL(file);

       this.messageService.add({
    severity: 'success',
    summary: 'Upload Success',
    detail: `${file.name} uploaded successfully`
  });

  }


  async startRecording() {

    try {

      const stream =
        await navigator.mediaDevices
          .getUserMedia({
            audio: true
          });

      this.audioChunks = [];

      this.mediaRecorder =
        new MediaRecorder(stream);

      this.mediaRecorder.start();

      this.isRecording = true;

      this.mediaRecorder.addEventListener(
        'dataavailable',
        (event) => {

          this.audioChunks.push(event.data);

        }
      );

      this.mediaRecorder.addEventListener(
        'stop',
        () => {

          this.recordedBlob = new Blob(
            this.audioChunks,
            {
              type: 'audio/webm'
            }
          );

          this.audioUrl =
            URL.createObjectURL(
              this.recordedBlob
            );

          this.selectedFileName =
            'recorded-audio.webm';

          this.selectedFile = new File(
            [this.recordedBlob],
            'recorded-audio.webm',
            {
              type: 'audio/webm'
            }
          );

        }
      );

    } catch (error) {

      console.error(error);

      alert(
        'Please allow microphone permission'
      );

    }

  }



  stopRecording() {

    if (this.mediaRecorder) {

      this.mediaRecorder.stop();

      this.isRecording = false;

    }

  }


  copyText(text: string) {

    navigator.clipboard.writeText(text);

  }


  processAudio() {

    if (!this.selectedFile) {

      alert(
        'Please upload or record audio'
      );

      return;

    }

    this.isProcessing = true;

    this.errorMessage = '';

    this.transcriptionService
      .transcribeAudio(this.selectedFile)
      .subscribe({

        next: (response: AudioResponse) => {

          console.log(response);

          this.malayalamText =
            response.malayalamText;

          this.englishText =
            response.englishText;

          this.isProcessing = false;

        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.Message
            || 'Failed to process audio';

          this.isProcessing = false;

        }

      });

  }

}