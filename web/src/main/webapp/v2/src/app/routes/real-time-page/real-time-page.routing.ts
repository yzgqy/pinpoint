
import { Routes } from '@angular/router';

import { UrlPath, UrlPathId } from 'app/shared/models';
import { RealTimePagingContainerComponent } from 'app/core/components/real-time/real-time-paging-container.component';
import { SystemConfigurationResolverService, ServerTimeResolverService } from 'app/shared/services';
import { RealTimePageComponent } from './real-time-page.component';
import { NewRealTimePagingContainerComponent } from 'app/core/components/real-time-new/new-real-time-paging-container.component';

const TO_MAIN = '/' + UrlPath.MAIN;

export const routing: Routes = [
    {
        path: '',
        component: RealTimePageComponent,
        resolve: {
            configuration: SystemConfigurationResolverService
        },
        children: [
            {
                path: '',
                redirectTo: TO_MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.APPLICATION,
                redirectTo: TO_MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.APPLICATION + '/:' + UrlPathId.PAGE,
                resolve: {
                    serverTime: ServerTimeResolverService
                },
                // component: RealTimePagingContainerComponent
                component: NewRealTimePagingContainerComponent
            }
        ]
    }
];
