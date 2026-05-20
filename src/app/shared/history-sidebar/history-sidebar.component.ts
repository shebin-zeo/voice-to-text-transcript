import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranscriptionService } from '../../core/services/transcription.service';

export interface HistoryItem {
  id: string;
  fileName: string;
  fileSize: number;
  malayalamText: string;
  englishText: string;
  status: string;
}

@Component({
  selector: 'app-history-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history-sidebar.component.html',
  styleUrl: './history-sidebar.component.scss'
})
export class HistorySidebarComponent implements OnInit {

  historyList: HistoryItem[] = [];
  selectedId: string | null = null;
  isCollapsed = false;
  isLoading = true;

  private historyService = inject(TranscriptionService);

  @Output() selectHistory = new EventEmitter<HistoryItem>();

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.isLoading = true;
    this.historyService.chatsHistory().subscribe({
      next: (res) => {
        this.historyList = res;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  openHistory(item: HistoryItem): void {
    this.selectedId = item.id;
    this.selectHistory.emit(item);
  }

  toggleCollapse(): void {
    this.isCollapsed = !this.isCollapsed;
  }

  formatSize(bytes: number): string {
    if (!bytes) return '';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }
}