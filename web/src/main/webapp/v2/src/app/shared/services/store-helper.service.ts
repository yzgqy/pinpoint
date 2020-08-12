import { Injectable } from '@angular/core';
import { Store, Action, select } from '@ngrx/store';
import { Observable, Subject, iif } from 'rxjs';
import { takeUntil, map, filter, debounceTime, distinctUntilChanged } from 'rxjs/operators';

import {
    AppState,
    STORE_KEY,
    selectServerMapDisableState,
    selectInfoPerServerVisibleState,
    selectTimelineRange,
    selectTimelineSelectedTime,
    selectTimelineSelectionRange
} from 'app/shared/store';
import { WebAppSettingDataService } from './web-app-setting-data.service';

@Injectable()
export class StoreHelperService {
    private dateFormatList: string[][];
    constructor(
        private store: Store<AppState>,
        private webAppSettingDataService: WebAppSettingDataService
    ) {
        this.dateFormatList = this.webAppSettingDataService.getDateFormatList();
    }
    getApplicationList(unsubscribe: Subject<void>): Observable<IApplication[]> {
        return this.getObservable(STORE_KEY.APPLICATION_LIST, unsubscribe);
    }
    getFavoriteApplicationList(unsubscribe: Subject<void>): Observable<IApplication[]> {
        return this.getObservable(STORE_KEY.FAVORITE_APPLICATION_LIST, unsubscribe);
    }
    getTimezone(unsubscribe?: Subject<void>): Observable<string> {
        return this.getObservable(STORE_KEY.TIMEZONE, unsubscribe);
    }
    getDateFormatIndex(unsubscribe: Subject<void>): Observable<number> {
        return this.getObservable(STORE_KEY.DATE_FORMAT, unsubscribe);
    }
    getDateFormat(unsubscribe: Subject<void>, index: number): Observable<string> {
        return this.getObservable(STORE_KEY.DATE_FORMAT, unsubscribe).pipe(
            map((format: number) => {
                return this.dateFormatList[format][index];
            })
        );
    }
    getDateFormatArray(unsubscribe: Subject<void>, ...index: number[]): Observable<string[]> {
        return this.getObservable(STORE_KEY.DATE_FORMAT, unsubscribe).pipe(
            map((format: number) => {
                return index.map((i: number) => {
                    return this.dateFormatList[format][i];
                });
            })
        );
    }
    getAgentList<T>(unsubscribe: Subject<void>): Observable<T> {
        return this.getObservable(STORE_KEY.ADMIN_AGENT_LIST, unsubscribe).pipe(
            filter((data: T) => {
                return data ? true : false;
            })
        );
    }
    getServerAndAgentQuery<T>(unsubscribe: Subject<void>): Observable<T> {
        return this.getObservable(STORE_KEY.SERVER_AND_AGENT, unsubscribe).pipe(
            debounceTime(100),
            distinctUntilChanged()
        );
    }
    getHoverInfo(unsubscribe: Subject<void>): Observable<IHoveredInfo> {
        return this.getObservable(STORE_KEY.HOVER_ON_INSPECTOR_CHARTS, unsubscribe);
    }
    getServerMapTargetSelectedByList(unsubscribe: Subject<void>): Observable<any> {
        return this.getObservable(STORE_KEY.SERVER_MAP_TARGET_SELECTED_BY_LIST, unsubscribe).pipe(
            filter((target: any) => {
                return (target && target.key) ? true : false;
            })
        );
    }
    getAgentSelection<T>(unsubscribe: Subject<void>): Observable<T> {
        return this.getObservable(STORE_KEY.AGENT_SELECTION, unsubscribe);
    }
    getAgentSelectionForServerList<T>(unsubscribe: Subject<void>): Observable<T> {
        return this.getObservable(STORE_KEY.AGENT_SELECTION_FOR_SERVER_LIST, unsubscribe);
    }
    getRealTimeScatterChartRange(unsubscribe: Subject<void>): Observable<IScatterXRange> {
        return this.getObservable(STORE_KEY.REAL_TIME_SCATTER_CHART, unsubscribe).pipe(
            filter((range: IScatterXRange) => {
                if (range && range.from && range.to && range.from !== -1 && range.to !== -1) {
                    return true;
                }
                return false;
            })
        );
    }
    getScatterChartData<T>(unsubscribe: Subject<void>): Observable<T> {
        return this.getObservable(STORE_KEY.SCATTER_CHART, unsubscribe);
    }
    getServerMapData<T>(unsubscribe: Subject<void>): Observable<T> {
        return this.getObservable(STORE_KEY.SERVER_MAP_DATA, unsubscribe);
    }
    getServerMapLoadingState(unsubscribe: Subject<void>): Observable<string> {
        return this.getObservable(STORE_KEY.SERVER_MAP_LOADING_STATE, unsubscribe);
    }
    getServerMapTargetSelected<ISelectedTarget>(unsubscribe: Subject<void>): Observable<ISelectedTarget> {
        return this.getObservable(STORE_KEY.SERVER_MAP_TARGET_SELECTED, unsubscribe);
    }
    getTransactionData(unsubscribe: Subject<void>): Observable<ITransactionMetaData> {
        return this.getObservable(STORE_KEY.TRANSACTION_DATA, unsubscribe);
    }
    getTransactionDetailData(unsubscribe: Subject<void>): Observable<ITransactionDetailData> {
        return this.getObservable(STORE_KEY.TRANSACTION_DETAIL_DATA, unsubscribe);
    }
    getServerListData(unsubscribe: Subject<void>): Observable<any> {
        return this.getObservable(STORE_KEY.SERVER_LIST, unsubscribe);
    }
    getAgentInfo(unsubscribe?: Subject<void>): Observable<IServerAndAgentData> {
        return this.getObservable(STORE_KEY.AGENT_INFO, unsubscribe);
    }
    getLoadChartYMax(unsubscribe: Subject<void>): Observable<number> {
        return this.getObservable(STORE_KEY.LOAD_CHART_Y_MAX, unsubscribe);
    }
    getResponseSummaryChartYMax(unsubscribe: Subject<void>): Observable<number> {
        return this.getObservable(STORE_KEY.RESPONSE_SUMMARY_CHART_Y_MAX, unsubscribe);
    }
    getServerMapDisableState(unsubscribe: Subject<void>): Observable<boolean> {
        return this.store.pipe(
            select(selectServerMapDisableState),
            takeUntil(unsubscribe)
        );
    }
    getInfoPerServerState(unsubscribe: Subject<void>): Observable<boolean> {
        return this.store.pipe(
            select(selectInfoPerServerVisibleState),
            takeUntil(unsubscribe)
        );
    }
    getInspectorTimelineData(unsubscribe: Subject<void>): Observable<ITimelineInfo> {
        return this.getObservable(STORE_KEY.TIMELINE, unsubscribe);
    }
    getInspectorTimelineRange(unsubscribe: Subject<void>): Observable<number[]> {
        return this.store.pipe(
            takeUntil(unsubscribe),
            select(selectTimelineRange),
            filter(([from, to]: number[]) => {
                return (from === 0 && to === 0) ? false : true;
            })
        );
    }
    getInspectorTimelineSelectionRange(unsubscribe: Subject<void>): Observable<number[]> {
        return this.store.pipe(
            takeUntil(unsubscribe),
            select(selectTimelineSelectionRange),
            filter(([from, to]: number[]) => {
                return (from === 0 && to === 0) ? false : true;
            })
        );
    }
    getInspectorTimelineSelectedTime(unsubscribe: Subject<void>): Observable<number> {
        return this.store.pipe(
            takeUntil(unsubscribe),
            select(selectTimelineSelectedTime),
            filter((time: number) => {
                return time === 0 ? false : true;
            })
        );
    }
    private getObservable(key: string, unsubscribe?: Subject<void>): Observable<any> {
        return iif(
            () => !!unsubscribe,
            this.store.pipe(
                select(key),
                takeUntil(unsubscribe)
            ),
            this.store.pipe(select(key))
        );
    }
    dispatch(action: Action): void {
        this.store.dispatch(action);
    }
}
