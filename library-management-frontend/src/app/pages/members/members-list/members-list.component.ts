import { Component, OnInit, OnDestroy } from '@angular/core';
import { MemberService } from '../../../services/member.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-members-list',
  templateUrl: './members-list.component.html',
  styleUrls: ['./members-list.component.css']
})
export class MembersListComponent implements OnInit, OnDestroy {
  members: any[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 1;
  pageSize = 10;
  searchQuery = '';
  loading = false;
  private destroy$ = new Subject<void>();

  constructor(
    private memberService: MemberService,
private messageService: MessageService  ) {}

  ngOnInit() {
    this.loadMembers();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadMembers() {
    this.loading = true;
    
    if (this.searchQuery) {
      this.search();
    } else {
      this.memberService.getMembers(this.currentPage - 1, this.pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.members = response.content;
            this.totalElements = response.totalElements;
            this.totalPages = response.totalPages;
            this.loading = false;
          },
          error: (error) => {
            console.error('Error loading members:', error);
            this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load members'
          });
            this.loading = false;
          }
        });
    }
  }

  search() {
    if (this.searchQuery.trim()) {
      this.loading = true;
      this.memberService.searchMembers(this.searchQuery, this.currentPage - 1, this.pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.members = response.content;
            this.totalElements = response.totalElements;
            this.totalPages = response.totalPages;
            this.loading = false;
          },
          error: (error) => {
            this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Search failed'
          });
          
            this.loading = false;
          }
        });
    } else {
      this.loadMembers();
    }
  }

  clearSearch() {
    this.searchQuery = '';
    this.currentPage = 1;
    this.loadMembers();
  }

  updateStatus(memberId: number, status: string) {
    this.loading = true;
    this.memberService.updateMemberStatus(memberId, status)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: `Member ${status.toLowerCase()} successfully`
});
          this.loadMembers(); // Reload after update
        },
        error: (error) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Failed to update member status'
          });
          this.loading = false;
        }
      });
  }

  deactivateMember(id: number) {
    if (confirm('Are you sure you want to deactivate this member?')) {
      this.loading = true;
      this.memberService.deactivateMember(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: 'Member deactivated successfully'
});
            this.loadMembers();
          },
          error: (error) => {
            this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Failed to deactivate member'
          });
            this.loading = false;
          }
        });
    }
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadMembers();
  }

  onSortChange(sortField: string) {
    // Implement sorting logic
    this.loadMembers();
  }
}