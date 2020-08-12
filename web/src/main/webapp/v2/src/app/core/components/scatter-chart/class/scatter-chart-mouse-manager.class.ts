import * as moment from 'moment-timezone';
import { Observable, Subject } from 'rxjs';
import { IOptions } from './scatter-chart.class';
import { ScatterChartSizeCoordinateManager } from './scatter-chart-size-coordinate-manager.class';

export class ScatterChartMouseManager {
    elementAxisWrapper: HTMLElement;
    elementXAxisLabelWrapper: HTMLElement;
    elementXAxisLabel: HTMLElement;
    elementYAxisLabelWrapper: HTMLElement;
    elementYAxisLabel: HTMLElement;
    elementDragWrapper: HTMLElement;
    elementDragArea: HTMLElement;
    private outDragArea: Subject<any>;
    onDragArea$: Observable<any>;
    constructor(
        private options: IOptions,
        private coordinateManager: ScatterChartSizeCoordinateManager,
        private elementContainer: HTMLElement
    ) {
        this.outDragArea = new Subject();
        this.onDragArea$ = this.outDragArea.asObservable();

        const padding = this.coordinateManager.getPadding();
        const areaWidth = this.coordinateManager.getWidth() - padding.left - padding.right;
        const areaHeight = this.coordinateManager.getHeight() - padding.top - padding.bottom;
        const redLineWidth = 10;

        this.initWrapperElement(areaWidth, areaHeight);
        this.initXLabelElement(areaHeight, redLineWidth);
        this.initYLabelElement(padding.left, redLineWidth);
        this.initDragElement();
        this.initEvent();
    }
    private initWrapperElement(areaWidth: number, areaHeight: number): void {
        this.elementAxisWrapper = document.createElement('div');
        this.elementAxisWrapper.setAttribute('style', `
            top: ${this.coordinateManager.getTopPadding()}px;
            left: ${this.coordinateManager.getLeftPadding()}px;
            width: ${areaWidth}px;
            height: ${areaHeight}px;
            cursor: crosshair;
            z-index: 600;
            position: absolute;
            background-color: rgba(0,0,0,0);
        `);
        this.elementAxisWrapper.setAttribute('class', 'overlay');
        this.elementContainer.appendChild(this.elementAxisWrapper);
    }
    private initXLabelElement(areaHeight: number, redLineWidth: number): void {
        this.elementXAxisLabelWrapper = document.createElement('div');
        this.elementXAxisLabelWrapper.draggable = false;
        this.elementXAxisLabelWrapper.setAttribute('style', `
            top: ${areaHeight + redLineWidth}px;
            left: 0px;
            color: #FFF;
            width: 80px;
            display: none;
            position: absolute;
            text-align: center;
            background: #000;
            font-family: monospace;
            margin-left: ${-(56 / 2)}px;
        ` + this.options.axisLabelStyle);
        this.elementXAxisLabel = document.createElement('span');
        const elementXLine = document.createElement('div');
        elementXLine.setAttribute('style', `
            top: ${-redLineWidth}px;
            left: 27px;
            height: ${redLineWidth}px;
            position: absolute;
            border-left: 1px solid red;
        `);
        this.elementXAxisLabelWrapper.appendChild(this.elementXAxisLabel);
        this.elementXAxisLabelWrapper.appendChild(elementXLine);
        this.elementAxisWrapper.appendChild(this.elementXAxisLabelWrapper);
    }
    private initYLabelElement(paddingLeft: number, redLineWidth: number): void {
        this.elementYAxisLabelWrapper = document.createElement('div');
        this.elementYAxisLabelWrapper.draggable = false;
        this.elementYAxisLabelWrapper.setAttribute('style', `
            top: 0px;
            left: ${-(paddingLeft - 2)}px;
            color: #fff;
            width: ${paddingLeft - 2 - redLineWidth}px;
            display: none;
            position: absolute;
            margin-top: ${-redLineWidth}px;
            text-align: right;
            background: #000;
            font-family: monospace;
            padding-right: 3px;
            vertical-align: middle;
        ` + this.options.axisLabelStyle);
        this.elementYAxisLabel = document.createElement('span');
        const elementYLine = document.createElement('div');
        elementYLine.setAttribute('style', `
            top: 9px;
            right: -10px;
            width: ${redLineWidth}px;
            position: absolute;
            border-top: 1px solid red;
        `);
        this.elementYAxisLabelWrapper.appendChild(this.elementYAxisLabel);
        this.elementYAxisLabelWrapper.appendChild(elementYLine);
        this.elementAxisWrapper.appendChild(this.elementYAxisLabelWrapper);
    }
    private initDragElement(): void {
        this.elementDragWrapper = document.createElement('div');
        this.elementDragWrapper.setAttribute('style', `
            top: 0px;
            left: 0px;
            width: ${this.coordinateManager.getWidth()}px;
            height: ${this.coordinateManager.getHeight()}px;
            cursor: crosshair;
            z-index: 601;
            position: absolute;
            background-color: rgba(0,0,0,0);
        `);
        this.elementDragArea = document.createElement('div');
        this.elementDragArea.setAttribute('style', `
            width: 0px;
            height: 0px;
            display: none;
            position: absolute;
            border: 1px solid #469AE4;
            background-color: rgba(237, 242, 248, 0.5);
        `);
        this.elementDragWrapper.appendChild(this.elementDragArea);
        this.elementContainer.appendChild(this.elementDragWrapper);
    }
    private initEvent() {
        const padding = this.coordinateManager.getPadding();
        const areaWidth = this.coordinateManager.getWidth();
        const areaHeight = this.coordinateManager.getHeight();
        const axisArea = {
            leftRevision: this.coordinateManager.getBubbleHalfSize() + padding.left,
            width: areaWidth - this.coordinateManager.getBubbleHalfSize() + padding.left - padding.right,
            topRevision: padding.top,
            height: areaHeight - padding.top - padding.bottom - this.coordinateManager.getBubbleHalfSize()
        };

        let startDrag = false;
        let dragStartX = 0;
        let dragStartY = 0;
        let calculatedOffsetX = 0;
        let calculatedOffsetY = 0;
        let previousDragX = -1;
        let previousDragY = -1;
        function forceMouseUp(userMouseUp: boolean) {
            startDrag = false;
            this.elementDragArea.style.display = 'none';
            if (userMouseUp) {
                const fromX = (calculatedOffsetX >= dragStartX ? dragStartX : calculatedOffsetX) - axisArea.leftRevision;
                const toX = (calculatedOffsetX >= dragStartX ? calculatedOffsetX : dragStartX) - axisArea.leftRevision;
                const fromY = (calculatedOffsetY >= dragStartY ? calculatedOffsetY : dragStartY) - axisArea.topRevision;
                const toY = (calculatedOffsetY >= dragStartY ? dragStartY : calculatedOffsetY) - axisArea.topRevision;
                this.outDragArea.next({
                    x: {
                        from: Math.max(fromX, 0),
                        to: Math.min(toX, axisArea.width)
                    },
                    y: {
                        // y값은 from과 to를 뒤집어서 보내야 함.
                        from: Math.max(axisArea.height - fromY, 0),
                        to: Math.min(axisArea.height - toY, axisArea.height)
                    }
                });
            }
            this.elementDragArea.style.top = '0px';
            this.elementDragArea.style.left = '0px';
            this.elementDragArea.style.width = '0px';
            this.elementDragArea.style.height = '0px';
            previousDragX = -1;
            previousDragY = -1;
        }
        this.elementDragWrapper.addEventListener('mousedown', (event: MouseEvent) => {
            startDrag = true;
            dragStartX = calculatedOffsetX = event.offsetX;
            dragStartY = calculatedOffsetY = event.offsetY;
            this.elementDragArea.style.display = 'block';
            this.elementDragArea.style.top = dragStartY + 'px';
            this.elementDragArea.style.left = dragStartX + 'px';
            event.preventDefault();
        });
        this.elementDragWrapper.addEventListener('mouseup', (event: MouseEvent) => {
            forceMouseUp.call(this, true);
            event.preventDefault();
        });

        this.elementDragWrapper.addEventListener('mousemove', (event: MouseEvent) => {
            calculatedOffsetX += event.movementX;
            calculatedOffsetY += event.movementY;
            if (startDrag) {
                this.checkAxisLabel(calculatedOffsetX, calculatedOffsetY, axisArea);
                if (previousDragX !== calculatedOffsetX) {
                    if (calculatedOffsetX <= areaWidth) {
                        if (calculatedOffsetX >= dragStartX) {
                            this.elementDragArea.style.left = dragStartX + 'px';
                            this.elementDragArea.style.width = (calculatedOffsetX - dragStartX) + 'px';
                        } else {
                            this.elementDragArea.style.left = calculatedOffsetX + 'px';
                            this.elementDragArea.style.width = (dragStartX - calculatedOffsetX) + 'px';
                        }
                        previousDragX = calculatedOffsetX;
                    }
                }
                if (previousDragY !== calculatedOffsetY) {
                    if (calculatedOffsetY <= areaHeight) {
                        if (calculatedOffsetY >= dragStartY) {
                            this.elementDragArea.style.top = dragStartY + 'px';
                            this.elementDragArea.style.height = (calculatedOffsetY - dragStartY) + 'px';
                        } else {
                            this.elementDragArea.style.top = calculatedOffsetY + 'px';
                            this.elementDragArea.style.height = (dragStartY - calculatedOffsetY) + 'px';
                        }
                        previousDragY = calculatedOffsetY;
                    }
                }
            } else {
                this.checkAxisLabel(event.offsetX, event.offsetY, axisArea);
            }
            event.preventDefault();
        });
        this.elementDragWrapper.addEventListener('mouseleave', (event: MouseEvent) => {
            forceMouseUp.call(this, false);
            this.hideAxisLabel();
            event.preventDefault();
            return false;
        });
    }
    private checkAxisLabel(offsetX: number, offsetY: number, axisArea: any): void {
        if (!this.options.showMouseGuideLine) {
            return;
        }
        const x = offsetX - axisArea.leftRevision;
        const y = offsetY - axisArea.topRevision;
        if (x >= 0 && x <= axisArea.width || y >= 0 && y <= axisArea.height) {
            if (x >= 0 && x <= axisArea.width) {
                this.showXAxisLabel();
            } else {
                this.hideXAxisLabel();
            }
            if (y >= 0 && y <= axisArea.height) {
                this.showYAxisLabel();
            } else {
                this.hideYAxisLabel();
            }
            this.setAndMoveAxisLabel(x, y);
        } else {
            this.hideAxisLabel();
        }
        // if (x >= 0 && x <= axisArea.width && y >= 0 && y <= axisArea.height) {
        //     this.showAxisLabel();
        //     this.setAndMoveAxisLabel(x, y);
        // } else {
        //     this.hideAxisLabel();
        // }
    }
    private setAndMoveAxisLabel(x: number, y: number): void {
        const height = this.coordinateManager.getHeight();
        const padding = this.coordinateManager.getPadding();
        const bubbleHalfSize = this.coordinateManager.getBubbleHalfSize();
        const xLabel = moment(this.coordinateManager.parseMouseXToXData(x - bubbleHalfSize)).tz(this.options.timezone).format(this.options.dateFormat[1]);
        const yLabel = this.coordinateManager.parseMouseYToYData(height - y - padding.bottom - padding.top - bubbleHalfSize);
        this.elementXAxisLabel.textContent = xLabel;
        this.elementYAxisLabel.textContent = yLabel.toLocaleString();
        this.elementXAxisLabelWrapper.style.left = (x + bubbleHalfSize) + 'px';
        this.elementYAxisLabelWrapper.style.top = (y + bubbleHalfSize) + 'px';
    }
    showXAxisLabel(): void {
        this.elementXAxisLabelWrapper.style.display = 'block';
    }
    showYAxisLabel(): void {
        this.elementYAxisLabelWrapper.style.display = 'block';
    }
    showAxisLabel(): void {
        this.showXAxisLabel();
        this.showYAxisLabel();
    }
    hideXAxisLabel(): void {
        this.elementXAxisLabelWrapper.style.display = 'none';
    }
    hideYAxisLabel(): void {
        this.elementYAxisLabelWrapper.style.display = 'none';
    }
    hideAxisLabel(): void {
        this.hideXAxisLabel();
        this.hideYAxisLabel();
    }
    // triggerDrag(welFakeSelectBox) {
    //     var oDragAreaPosition = this._adjustSelectBoxForChart(welFakeSelectBox);
    //     this._oCallback.onSelect(oDragAreaPosition, this._parseCoordinatesToXY(oDragAreaPosition));
    // }
    // _adjustSelectBoxForChart(welSelectBox) {
    //     var oPadding = this._oSCManager.getPadding();
    //     var bubbleSize = this._oSCManager.getBubbleSize();
    //     var nMinTop = oPadding.top + bubbleSize;
    //     var nMinLeft = oPadding.left + bubbleSize;
    //     var nMaxRight = this._oSCManager.getWidth() - oPadding.right - bubbleSize;
    //     var nMaxBottom = this._oSCManager.getHeight() - oPadding.bottom - bubbleSize;

    //     var nLeft = parseInt(welSelectBox.css("left"), 10);
    //     var nRight = nLeft + welSelectBox.width();
    //     var nTop = parseInt(welSelectBox.css("top"), 10);
    //     var nBottom = nTop + welSelectBox.height();

    //     nTop = Math.max(nTop, nMinTop);
    //     nLeft = Math.max(nLeft, nMinLeft);
    //     nRight = Math.min(nRight, nMaxRight);
    //     nBottom = Math.min(nBottom, nMaxBottom);

    //     var oNextInfo = {
    //         "top": nTop,
    //         "left": nLeft,
    //         "width": nRight - nLeft,
    //         "height": nBottom - nTop
    //     };
    //     welSelectBox.animate(oNextInfo, 200);
    //     return oNextInfo;
    // }
    // _parseCoordinatesToXY(oPosition) {
    //     var oPadding = this._oSCManager.getPadding();
    //     var bubbleSize = this._oSCManager.getBubbleSize();
    //     return {
    //         "fromX": this._oSCManager.parseMouseXToXData(oPosition.left - oPadding.left - bubbleSize),
    //         "toX": this._oSCManager.parseMouseXToXData(oPosition.left + oPosition.width - oPadding.left - bubbleSize),
    //         "fromY": this._oSCManager.parseMouseYToYData(this._oSCManager.getHeight() - (oPadding.bottom + bubbleSize) - (oPosition.top + oPosition.height)),
    //         "toY": this._oSCManager.parseMouseYToYData(this._oSCManager.getHeight() - (oPadding.bottom + bubbleSize) - oPosition.top)
    //     };
    // }
}
