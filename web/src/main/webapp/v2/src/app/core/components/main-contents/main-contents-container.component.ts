import { Component, OnInit } from '@angular/core';

import { DynamicPopupService } from 'app/shared/services';
import { HELP_VIEWER_LIST, HelpViewerPopupContainerComponent } from 'app/core/components/help-viewer-popup/help-viewer-popup-container.component';

@Component({
    selector: 'pp-main-contents-container',
    templateUrl: './main-contents-container.component.html',
    styleUrls: ['./main-contents-container.component.css']
})
export class MainContentsContainerComponent implements OnInit {
    constructor(
        private dynamicPopupService: DynamicPopupService
    ) {}
    ngOnInit() {}
    onShowHelp($event: MouseEvent): void {
        const {left, top, width, height} = ($event.target as HTMLElement).getBoundingClientRect();

        this.dynamicPopupService.openPopup({
            data: HELP_VIEWER_LIST.SERVER_MAP,
            coord: {
                coordX: left + width / 2,
                coordY: top + height / 2
            },
            component: HelpViewerPopupContainerComponent
        });
    }
}
