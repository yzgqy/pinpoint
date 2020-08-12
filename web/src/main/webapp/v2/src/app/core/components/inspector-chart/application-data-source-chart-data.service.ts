import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IChartDataService, IChartDataFromServer } from 'app/core/components/inspector-chart/chart-data.service';
import { NewUrlStateNotificationService } from 'app/shared/services';
import { UrlPathId } from 'app/shared/models';
import { getParamForApplicationChartData } from 'app/core/utils/chart-data-param-maker';

export interface IApplicationDataSourceChart extends IChartDataFromServer {
    jdbcUrl: string;
    serviceType: string;
}

@Injectable()
export class ApplicationDataSourceChartDataService implements IChartDataService {
    private requestURL = 'getApplicationStat/dataSource/chart.pinpoint';

    constructor(
        private http: HttpClient,
        private newUrlStateNotificationService: NewUrlStateNotificationService,
    ) {}

    getData(range: number[]): Observable<IChartDataFromServer[] | AjaxException> {
        return this.http.get<IChartDataFromServer[] | AjaxException>(this.requestURL,
            getParamForApplicationChartData(this.newUrlStateNotificationService.getPathValue(UrlPathId.APPLICATION).getApplicationName(), range)
        );
    }
}
