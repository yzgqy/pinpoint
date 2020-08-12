
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClipboardModule } from 'ngx-clipboard';

import { ClickOutsideModule } from 'ng-click-outside';
import { TranslateReplaceService } from './services/translate-replace.service';
import { ServerTimeDataService } from './services/server-time-data.service';
import { WebAppSettingDataService } from './services/web-app-setting-data.service';
import { ComponentDefaultSettingDataService } from './services/component-default-setting-data.service';
import { RouteInfoCollectorService } from './services/route-info-collector.service';
import { ServerTimeResolverService } from './services/server-time-resolver.service';
import { NewUrlStateNotificationService } from './services/new-url-state-notification.service';
import { UrlRouteManagerService } from './services/url-route-manager.service';
import { SystemConfigurationDataService } from './services/system-configuration-data.service';
import { SystemConfigurationResolverService } from './services/system-configuration-resolver.service';
import { SplitRatioService } from './services/split-ratio.service';
import { GutterEventService } from './services/gutter-event.service';
import { AjaxExceptionCheckerService } from './services/ajax-exception-checker.service';
import { ApplicationListResolverService } from './services/application-list-resolver.service';
import { AnalyticsService } from './services/analytics.service';
import { BrowserSupportCheckService } from './services/browser-support-check.service';
import { AgentHistogramDataService } from './services/agent-histogram-data.service';
import { TransactionDetailDataService } from './services/transaction-detail-data.service';
import { TransactionViewTypeService } from './services/transaction-view-type.service';
import { StoreHelperService } from './services/store-helper.service';
import { UrlValidateGuard } from './services/url-validate.guard';

import { HeaderLogoComponent } from './components/header-logo';
import { EmptyContentsComponent, NoneComponent } from './components/empty-contents';
import { UrlRedirectorComponent } from './components/url-redirector';
import { LoadingComponent } from './components/loading';
import { FilmForDisableComponent } from './components/film-for-disable';
import { SimpleProgressSliderComponent } from './components/simple-progress-slider';
import { SettingHeightDirective } from './directives/setting-height.directive';
import { ContextPopupDirective } from './directives/context-popup.directive';
import { SplitterDirective } from './directives/splitter.directive';
import { SearchInputDirective } from './directives/search-input.directive';
import { SafeHtmlPipe } from './pipes/safe-html.pipe';
import { JSONTextParserPipe } from './pipes/json-text-parser.pipe';
import { DynamicPopupService } from 'app/shared/services/dynamic-popup.service';

@NgModule({
    declarations: [
        NoneComponent,
        HeaderLogoComponent,
        EmptyContentsComponent,
        UrlRedirectorComponent,
        LoadingComponent,
        FilmForDisableComponent,
        SimpleProgressSliderComponent,
        SafeHtmlPipe,
        JSONTextParserPipe,
        SettingHeightDirective,
        ContextPopupDirective,
        SplitterDirective,
        SearchInputDirective
    ],
    imports: [
        CommonModule,
        ClickOutsideModule
    ],
    exports: [
        CommonModule,
        FormsModule,
        ClipboardModule,
        ClickOutsideModule,
        HeaderLogoComponent,
        EmptyContentsComponent,
        UrlRedirectorComponent,
        LoadingComponent,
        FilmForDisableComponent,
        SimpleProgressSliderComponent,
        SafeHtmlPipe,
        JSONTextParserPipe,
        SettingHeightDirective,
        ContextPopupDirective,
        SplitterDirective,
        SearchInputDirective
    ],
    providers: [
        TranslateReplaceService,
        ServerTimeDataService,
        ServerTimeResolverService,
        ComponentDefaultSettingDataService,
        RouteInfoCollectorService,
        WebAppSettingDataService,
        NewUrlStateNotificationService,
        UrlRouteManagerService,
        SystemConfigurationDataService,
        SystemConfigurationResolverService,
        SplitRatioService,
        GutterEventService,
        AjaxExceptionCheckerService,
        ApplicationListResolverService,
        AnalyticsService,
        BrowserSupportCheckService,
        AgentHistogramDataService,
        TransactionDetailDataService,
        TransactionViewTypeService,
        StoreHelperService,
        UrlValidateGuard,
        DynamicPopupService
    ]
})
export class SharedModule { }
