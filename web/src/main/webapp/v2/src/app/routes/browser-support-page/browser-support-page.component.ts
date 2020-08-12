import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import * as bowser from 'bowser';

import { WebAppSettingDataService } from 'app/shared/services';

interface IBrowserInfo {
    downloadLink: string;
    name: string;
    displayName: string;
}

@Component({
    templateUrl: './browser-support-page.component.html',
    styleUrls: ['./browser-support-page.component.css']
})
export class BrowserSupportPageComponent implements OnInit {
    private browserInfoList: IBrowserInfo[];
    funcImagePath: Function;
    i18nText$: Observable<string>;

    constructor(
        private webAppSettingDataService: WebAppSettingDataService,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.funcImagePath = this.webAppSettingDataService.getImagePathMakeFunc();
        this.browserInfoList = [
            {
                downloadLink: 'https://www.google.com/chrome',
                name: 'chrome',
                displayName: 'Google Chrome'
            }, {
                downloadLink: 'https://www.mozilla.org/en/firefox/new',
                name: 'firefox',
                displayName: 'Mozilla Firefox'
            }, {
                downloadLink: 'https://support.apple.com/en-us/HT204416',
                name: 'safari',
                displayName: 'Apple Safari'
            }, {
                downloadLink: 'https://www.microsoft.com/en-us/windows/microsoft-edge',
                name: 'edge',
                displayName: 'Microsoft Edge'
            }
        ];
        this.i18nText$ = this.translateService.get('SUPPORT.INSTALL_GUIDE');
    }

    getFilteredBrowserInfoList(): IBrowserInfo[] {
        const userOSName = bowser.osname;

        return this.browserInfoList.filter((browserInfo: IBrowserInfo) => {
            switch (userOSName) {
                case 'Windows':
                    return browserInfo.name !== 'safari';
                case 'macOS':
                    return browserInfo.name !== 'edge';
                default:
                    return browserInfo.name === 'chrome' || browserInfo.name === 'firefox';
            }
        });
    }
}
