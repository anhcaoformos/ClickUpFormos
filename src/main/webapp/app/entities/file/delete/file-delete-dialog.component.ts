import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IFile } from '../file.model';
import { FileService } from '../service/file.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './file-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FileDeleteDialogComponent {
  file?: IFile;

  constructor(protected fileService: FileService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
