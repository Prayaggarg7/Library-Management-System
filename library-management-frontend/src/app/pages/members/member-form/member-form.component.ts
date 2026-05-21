import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MemberService } from '../../../services/member.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-member-form',
  templateUrl: './member-form.component.html',
  styleUrls: ['./member-form.component.css']
})
export class MemberFormComponent implements OnInit {
  memberForm!: FormGroup;
  isEditMode = false;
  memberId: number | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private memberService: MemberService,
    private route: ActivatedRoute,
    private router: Router,
private messageService: MessageService   ) {}

  ngOnInit() {
    this.initForm();
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.memberId = +params['id'];
        this.loadMember();
      }
    });
  }

  initForm() {
    this.memberForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(255)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      status: ['ACTIVE']
    });
  }

  loadMember() {
    this.loading = true;
    if (this.memberId) {
      this.memberService.getMemberById(this.memberId).subscribe({
        next: (member: any) => {
          this.memberForm.patchValue(member);
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load member'
          });
          this.router.navigate(['/members']);
          this.loading = false;
        }
      });
    }
  }

  onSubmit() {
    if (this.memberForm.valid) {
      this.loading = true;
      const memberData = this.memberForm.value;
      
      const request = this.isEditMode && this.memberId
        ? this.memberService.updateMember(this.memberId, memberData)
        : this.memberService.createMember(memberData);

      request.subscribe({
        next: () => {
                      this.messageService.add({
  severity: 'success',
  summary: 'Success',
  detail: this.isEditMode ? 'Member updated successfully' : 'Member created successfully'
});
          this.router.navigate(['/members']);
          this.loading = false;
        },
        error: (error: any) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error.error?.message || 'Operation failed'
          });
          this.loading = false;
        }
      });
    } else {
      Object.keys(this.memberForm.controls).forEach(key => {
        this.memberForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel() {
    this.router.navigate(['/members']);
  }
}