import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'statusColor'
})
export class StatusColorPipe implements PipeTransform {
  transform(status: string): string {
    switch(status) {
      case 'ACTIVE':
        return 'text-success';
      case 'INACTIVE':
        return 'text-danger';
      case 'ISSUED':
        return 'text-warning';
      case 'RETURNED':
        return 'text-info';
      case 'OVERDUE':
        return 'text-danger fw-bold';
      default:
        return 'text-secondary';
    }
  }
}