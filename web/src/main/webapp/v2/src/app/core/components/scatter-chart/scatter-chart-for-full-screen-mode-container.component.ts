import { Component, ChangeDetectionStrategy, ChangeDetectorRef, OnInit, OnDestroy, ViewChild, ElementRef, Renderer2 } from '@angular/core';
import { Observable, Subject, of, combineLatest, Subscription } from 'rxjs';
import { takeUntil, delay } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

import {
    StoreHelperService,
    WebAppSettingDataService,
    NewUrlStateNotificationService,
    UrlRouteManagerService,
    AnalyticsService,
    TRACKED_EVENT_LIST,
    DynamicPopupService
} from 'app/shared/services';
import { Actions } from 'app/shared/store';
import { UrlPath, UrlPathId } from 'app/shared/models';
import { ScatterChartDataService } from './scatter-chart-data.service';
import { ScatterChart } from './class/scatter-chart.class';
import { ScatterChartInteractionService } from './scatter-chart-interaction.service';
import { HELP_VIEWER_LIST, HelpViewerPopupContainerComponent } from 'app/core/components/help-viewer-popup/help-viewer-popup-container.component';

@Component({
    selector: 'pp-scatter-chart-for-full-screen-mode-container',
    templateUrl: './scatter-chart-for-full-screen-mode-container.component.html',
    styleUrls: ['./scatter-chart-for-full-screen-mode-container.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ScatterChartForFullScreenModeContainerComponent implements OnInit, OnDestroy {
    @ViewChild('layerBackground') layerBackground: ElementRef;
    private unsubscribe: Subject<null> = new Subject();
    instanceKey = 'full-screen-mode';
    addWindow = true;
    i18nText: { [key: string]: string };
    selectedTarget: ISelectedTarget;
    selectedApplication: string;
    scatterDataServiceSubscription: Subscription;
    hideSettingPopup = true;
    selectedAgent: string;
    typeInfo = [{
        name: 'failed',
        color: '#E95459',
        order: 10
    }, {
        name: 'success',
        color: '#34B994',
        order: 20
    }];
    typeCount: object;
    width = 690;
    height = 345;
    min: number;
    max: number;
    fromX: number;
    toX: number;
    fromY: number;
    toY: number;
    application: string;
    scatterChartMode: string;
    timezone$: Observable<string>;
    dateFormat: string[];
    constructor(
        private changeDetectorRef: ChangeDetectorRef,
        private renderer: Renderer2,
        private translateService: TranslateService,
        private storeHelperService: StoreHelperService,
        private webAppSettingDataService: WebAppSettingDataService,
        private newUrlStateNotificationService: NewUrlStateNotificationService,
        private urlRouteManagerService: UrlRouteManagerService,
        private scatterChartDataService: ScatterChartDataService,
        private scatterChartInteractionService: ScatterChartInteractionService,
        private analyticsService: AnalyticsService,
        private dynamicPopupService: DynamicPopupService
    ) {}
    ngOnInit() {
        this.setScatterY();
        this.getI18NText();
        this.connectStore();
        this.newUrlStateNotificationService.onUrlStateChange$.pipe(
            takeUntil(this.unsubscribe)
        ).subscribe((urlService: NewUrlStateNotificationService) => {
            this.scatterChartMode = urlService.isRealTimeMode() ? ScatterChart.MODE.REALTIME : ScatterChart.MODE.STATIC;
            this.scatterChartDataService.setCurrentMode(this.scatterChartMode);
            this.selectedApplication = this.application = urlService.getPathValue(UrlPathId.APPLICATION).getKeyStr();
            this.selectedAgent = urlService.hasValue(UrlPathId.AGENT_ID) ? urlService.getPathValue(UrlPathId.AGENT_ID) : '';
            this.fromX = urlService.getStartTimeToNumber();
            this.toX = urlService.getEndTimeToNumber();
            of(1).pipe(delay(1)).subscribe((num: number) => {
                this.scatterChartInteractionService.reset(this.instanceKey, this.selectedApplication, this.selectedAgent, this.fromX, this.toX, this.scatterChartMode);
                this.getScatterData();
                this.changeDetectorRef.detectChanges();
            });
        });
        this.scatterChartDataService.outScatterData$.pipe(
            takeUntil(this.unsubscribe)
        ).subscribe((scatterData: IScatterData) => {
            if (this.scatterChartMode === ScatterChart.MODE.STATIC) {
                this.scatterChartInteractionService.addChartData(this.instanceKey, scatterData);
                if (scatterData.complete === false) {
                    this.scatterChartDataService.loadData(
                        this.selectedApplication.split('^')[0],
                        this.fromX,
                        scatterData.resultFrom - 1,
                        this.getGroupUnitX(),
                        this.getGroupUnitY(),
                        false
                    );
                }
            } else {
                if (scatterData.reset === true) {
                    this.fromX = scatterData.currentServerTime - this.webAppSettingDataService.getSystemDefaultPeriod().getMiliSeconds();
                    this.toX = scatterData.currentServerTime;
                    this.scatterChartInteractionService.reset(this.instanceKey, this.selectedApplication, this.selectedAgent, this.fromX, this.toX, this.scatterChartMode);
                    of(1).pipe(delay(1000)).subscribe((useless: number) => {
                        this.getScatterData();
                    });
                } else {
                    this.scatterChartInteractionService.addChartData(this.instanceKey, scatterData);
                }
            }
        });
    }
    ngOnDestroy() {
        if (this.scatterDataServiceSubscription) {
            this.scatterDataServiceSubscription.unsubscribe();
        }
        this.unsubscribe.next();
        this.unsubscribe.complete();
    }
    private setScatterY(): void {
        const scatterYData = this.webAppSettingDataService.getScatterY(this.instanceKey);
        this.fromY = scatterYData.min;
        this.toY = scatterYData.max;
    }
    private getI18NText(): void {
        combineLatest(
            this.translateService.get('COMMON.NO_DATA'),
            this.translateService.get('COMMON.FAILED_TO_FETCH_DATA')
        ).subscribe((i18n: string[]) => {
            this.i18nText = {
                NO_DATA: i18n[0],
                FAILED_TO_FETCH_DATA: i18n[1]
            };
        });
    }
    private connectStore(): void {
        this.timezone$ = this.storeHelperService.getTimezone(this.unsubscribe);
        this.storeHelperService.getDateFormatArray(this.unsubscribe, 3, 4).subscribe((dateFormat: string[]) => {
            this.dateFormat = dateFormat;
        });
    }
    getScatterData(): void {
        if (this.scatterChartMode === ScatterChart.MODE.STATIC) {
            this.scatterChartDataService.loadData(
                this.selectedApplication.split('^')[0],
                this.fromX,
                this.toX,
                this.getGroupUnitX(),
                this.getGroupUnitY()
            );
        } else {
            this.scatterChartDataService.loadRealTimeData(
                this.selectedApplication.split('^')[0],
                this.fromX,
                this.toX,
                this.getGroupUnitX(),
                this.getGroupUnitY()
            );
        }
    }
    getGroupUnitX(): number {
        return Math.round((this.toX - this.fromX) / this.width);
    }
    getGroupUnitY(): number {
        return Math.round((this.toY - this.fromY) / this.height);
    }
    isHideSettingPopup(): boolean {
        return this.hideSettingPopup;
    }
    onApplySetting(params: any): void {
        this.fromY = params.min;
        this.toY = params.max;
        this.scatterChartInteractionService.changeYRange({
            instanceKey: this.instanceKey,
            from: params.min,
            to: params.max
        });
        this.onHideSetting();
        this.webAppSettingDataService.setScatterY(this.instanceKey, { min: params.min, max: params.max });
    }
    onHideSetting(): void {
        this.renderer.setStyle(this.layerBackground.nativeElement, 'display', 'none');
        this.hideSettingPopup = true;
    }
    onShowSetting(): void {
        this.analyticsService.trackEvent(TRACKED_EVENT_LIST.CLICK_SCATTER_SETTING);
        this.renderer.setStyle(this.layerBackground.nativeElement, 'display', 'block');
        this.hideSettingPopup = false;
    }
    onDownload(): void {
        this.analyticsService.trackEvent(TRACKED_EVENT_LIST.DOWNLOAD_SCATTER);
        this.scatterChartInteractionService.downloadChart(this.instanceKey);
    }
    onShowHelp($event: MouseEvent): void {
        this.analyticsService.trackEvent(TRACKED_EVENT_LIST.TOGGLE_HELP_VIEWER, HELP_VIEWER_LIST.SCATTER);
        const {left, top, width, height} = ($event.target as HTMLElement).getBoundingClientRect();

        this.dynamicPopupService.openPopup({
            data: HELP_VIEWER_LIST.SCATTER,
            coord: {
                coordX: left + width / 2,
                coordY: top + height / 2
            },
            component: HelpViewerPopupContainerComponent
        });
    }
    onChangedSelectType(params: {instanceKey: string, name: string, checked: boolean}): void {
        this.analyticsService.trackEvent(TRACKED_EVENT_LIST.CHANGE_SCATTER_CHART_STATE, `${params.name}: ${params.checked ? `on` : `off`}`);
        this.scatterChartInteractionService.changeViewType(params);
    }
    onChangeTransactionCount(params: object): void {
        this.typeCount = params;
    }
    onChangeRangeX(params: IScatterXRange): void {
        this.storeHelperService.dispatch(new Actions.UpdateRealTimeScatterChartXRange(params));
    }
    onSelectArea(params: any): void {
        this.analyticsService.trackEvent(TRACKED_EVENT_LIST.OPEN_TRANSACTION_LIST);
        this.urlRouteManagerService.openPage([
            UrlPath.TRANSACTION_LIST,
            this.newUrlStateNotificationService.getPathValue(UrlPathId.APPLICATION).getUrlStr(),
            this.newUrlStateNotificationService.getPathValue(UrlPathId.PERIOD).getValueWithTime(),
            this.newUrlStateNotificationService.getPathValue(UrlPathId.END_TIME).getEndTime()
        ], `${this.selectedApplication}|${params.x.from}|${params.x.to}|${params.y.from}|${params.y.to}|${this.selectedAgent}|${params.type.join(',')}`);
    }
}
