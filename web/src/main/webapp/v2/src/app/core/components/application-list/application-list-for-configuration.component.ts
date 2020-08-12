import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'pp-application-list-for-configuration',
    templateUrl: './application-list-for-configuration.component.html',
    styleUrls: ['./application-list-for-configuration.component.css']
})
export class ApplicationListForConfigurationComponent implements OnInit {
    @Input() applicationList: IApplication[];
    @Input() emptyText: string;
    @Input() funcImagePath: Function;
    @Input() iconBtnClassName: string;
    @Output() outSelectApp = new EventEmitter<IApplication>();

    constructor() {}
    ngOnInit() {}

    getIconPath(serviceType: string): string {
        return this.funcImagePath(serviceType);
    }

    onSelectApp(app: IApplication): void {
        this.outSelectApp.emit(app);
    }

    isListEmpty(): boolean {
        return this.applicationList.length === 0;
    }
}
