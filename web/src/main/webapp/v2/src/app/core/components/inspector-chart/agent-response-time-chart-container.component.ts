import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef  } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment-timezone';

import { Actions } from 'app/shared/store';
import { WebAppSettingDataService, NewUrlStateNotificationService, AjaxExceptionCheckerService, AnalyticsService, StoreHelperService, DynamicPopupService } from 'app/shared/services';
import { AgentResponseTimeChartDataService } from './agent-response-time-chart-data.service';
import { HELP_VIEWER_LIST } from 'app/core/components/help-viewer-popup/help-viewer-popup-container.component';
import { InspectorChartContainer } from 'app/core/components/inspector-chart/inspector-chart-container';
import { IChartDataFromServer } from 'app/core/components/inspector-chart/chart-data.service';

@Component({
    selector: 'pp-agent-response-time-chart-container',
    templateUrl: './agent-response-time-chart-container.component.html',
    styleUrls: ['./agent-response-time-chart-container.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AgentResponseTimeChartContainerComponent extends InspectorChartContainer implements OnInit, OnDestroy {
    constructor(
        storeHelperService: StoreHelperService,
        changeDetector: ChangeDetectorRef,
        webAppSettingDataService: WebAppSettingDataService,
        newUrlStateNotificationService: NewUrlStateNotificationService,
        chartDataService: AgentResponseTimeChartDataService,
        translateService: TranslateService,
        ajaxExceptionCheckerService: AjaxExceptionCheckerService,
        analyticsService: AnalyticsService,
        dynamicPopupService: DynamicPopupService
    ) {
        super(
            100,
            storeHelperService,
            changeDetector,
            webAppSettingDataService,
            newUrlStateNotificationService,
            chartDataService,
            translateService,
            ajaxExceptionCheckerService,
            analyticsService,
            dynamicPopupService
        );
    }

    ngOnInit() {
        this.initI18nText();
        this.initHoveredInfo();
        this.initTimezoneAndDateFormat();
        this.initChartData();
    }

    ngOnDestroy() {
        this.unsubscribe.next();
        this.unsubscribe.complete();
    }

    protected makeChartData(chartData: IChartDataFromServer): {[key: string]: any} {
        const xArr = [];
        const avgArr = [];
        const maxArr = [];

        const xData = chartData.charts.x;
        const responseAVG = chartData.charts.y['AVG'];
        const responseMAX = chartData.charts.y['MAX'];
        const dataCount = xData.length;

        for ( let i = 0 ; i < dataCount ; i++ ) {
            xArr.push(moment(xData[i]).tz(this.timezone).format(this.dateFormat[0]) + '#' + moment(xData[i]).tz(this.timezone).format(this.dateFormat[1]));
            if ( responseAVG.length === 0 ) {
                continue;
            }
            avgArr.push(this.parseData(responseAVG[i][2]));
            maxArr.push(this.parseData(responseMAX[i][1]));
        }
        return {
            x: xArr,
            avg: avgArr,
            max: maxArr,
        };
    }

    protected makeDataOption(data: {[key: string]: any}): {[key: string]: any} {
        return {
            labels: data.x,
            datasets: [{
                label: 'Avg',
                data: data.avg,
                fill: true,
                borderWidth: 0.5,
                borderColor: 'rgb(44, 160, 44)',
                backgroundColor: 'rgba(44, 160, 44, 0.4)',
                pointRadius: 0,
                pointHoverRadius: 3
            }, {
                label: 'Max',
                data: data.max,
                fill: true,
                borderWidth: 0.5,
                borderColor: 'rgb(246, 145, 36)',
                backgroundColor: 'rgba(246, 145, 36, 0.4)',
                pointRadius: 0,
                pointHoverRadius: 3
            }
        ]
        };
    }

    protected makeNormalOption(data: {[key: string]: any}): {[key: string]: any} {
        return {
            responsive: true,
            title: {
                display: false,
                text: 'Response Time'
            },
            tooltips: {
                mode: 'index',
                intersect: false,
                callbacks: {
                    title: (value: {[key: string]: any}[]): string => {
                        return value[0].xLabel.join(' ');
                    },
                    label: (value: {[key: string]: any}, d: {[key: string]: any}): string => {
                        return `${d.datasets[value.datasetIndex].label}: ${isNaN(value.yLabel) ? `-` : this.convertWithUnit(value.yLabel)}`;
                    }
                }
            },
            hover: {
                mode: 'index',
                intersect: false,
                onHover: (event: MouseEvent, elements: {[key: string]: any}[]): void => {
                    if (!this.isDataEmpty(data)) {
                        this.storeHelperService.dispatch(new Actions.ChangeHoverOnInspectorCharts({
                            index: event.type === 'mouseout' ? -1 : elements[0]._index,
                            offsetX: event.offsetX,
                            offsetY: event.offsetY
                        }));
                    }
                },
            },
            scales: {
                xAxes: [{
                    display: true,
                    scaleLabel: {
                        display: false
                    },
                    gridLines: {
                        color: 'rgb(0, 0, 0)',
                        lineWidth: 0.5,
                        drawBorder: true,
                        drawOnChartArea: false
                    },
                    ticks: {
                        maxTicksLimit: 4,
                        callback: (label: string): string[] => {
                            return label.split('#');
                        },
                        maxRotation: 0,
                        minRotation: 0,
                        fontSize: 11,
                        padding: 5
                    }
                }],
                yAxes: [{
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: 'Response Time (ms)',
                        fontSize: 14,
                        fontStyle: 'bold'
                    },
                    gridLines: {
                        color: 'rgb(0, 0, 0)',
                        lineWidth: 0.5,
                        drawBorder: true,
                        drawOnChartArea: false
                    },
                    ticks: {
                        beginAtZero: true,
                        maxTicksLimit: 5,
                        callback: (label: number): string => {
                            return this.convertWithUnit(label);
                        },
                        min: 0,
                        max: this.isDataEmpty(data) ? this.defaultYMax : undefined,
                        padding: 5
                    }
                }]
            },
            legend: {
                display: true,
                labels: {
                    boxWidth: 30,
                    padding: 10
                }
            }
        };
    }

    private convertWithUnit(value: number): string {
        const unit = ['ms', 'sec', 'min'];
        let result = value;
        let index = 0;
        while ( result >= 1000 ) {
            index++;
            result /= 1000;
        }

        result = Number.isInteger(result) ? result : Number(result.toFixed(2));
        return result + unit[index];
    }

    onShowHelp($event: MouseEvent): void {
        super.onShowHelp($event, HELP_VIEWER_LIST.AGENT_RESPONSE_TIME);
    }
}
