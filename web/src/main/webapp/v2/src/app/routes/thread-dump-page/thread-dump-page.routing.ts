
import { Routes } from '@angular/router';
import { UrlPath, UrlPathId } from 'app/shared/models';
import { ThreadDumpListContainerComponent } from 'app/core/components/thread-dump-list/thread-dump-list-container.component';
import { UrlRedirectorComponent } from 'app/shared/components/url-redirector';
import { SystemConfigurationResolverService, ServerTimeResolverService } from 'app/shared/services';
import { ThreadDumpPageComponent } from './thread-dump-page.component';

export const routing: Routes = [
    {
        path: '',
        component: ThreadDumpPageComponent,
        resolve: {
            configuration: SystemConfigurationResolverService
        },
        children: [
            {
                path: '',
                redirectTo: '/' + UrlPath.MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.APPLICATION,
                data: {
                    path: UrlPath.MAIN
                },
                component: UrlRedirectorComponent
            },
            {
                path: ':' + UrlPathId.APPLICATION +  '/:' + UrlPathId.AGENT_ID,
                data: {
                    path: UrlPath.MAIN
                },
                component: UrlRedirectorComponent
            },
            {
                path: ':' + UrlPathId.APPLICATION + '/:' + UrlPathId.AGENT_ID + '/:' + UrlPathId.FOCUS_TIMESTAMP,
                resolve: {
                    serverTime: ServerTimeResolverService
                },
                component: ThreadDumpListContainerComponent
            }
        ]
    }
];
