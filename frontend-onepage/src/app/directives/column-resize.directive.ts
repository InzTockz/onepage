import { Directive, ElementRef, EventEmitter, Input, OnDestroy, Output } from '@angular/core';

@Directive({
  selector: '[appColumnResize]',
  standalone: true,
  host: {
    class: 'column-resize-handle',
    role: 'separator',
    tabindex: '0',
    'aria-orientation': 'vertical',
    '[attr.aria-label]': "'Cambiar ancho de ' + columnLabel",
    '[attr.aria-valuenow]': 'width',
    '[attr.aria-valuemin]': 'minWidth',
    '[attr.aria-valuemax]': 'maxWidth',
    '[attr.aria-valuetext]': "width + ' px'",
    '[class.is-resizing]': 'isResizing',
    '(pointerdown)': 'onPointerDown($event)',
    '(pointermove)': 'onPointerMove($event)',
    '(pointerup)': 'onPointerUp($event)',
    '(pointercancel)': 'onPointerCancel($event)',
    '(lostpointercapture)': 'onPointerCancel($event)',
    '(keydown)': 'onKeyDown($event)',
    '(mousedown)': '$event.stopPropagation()',
    '(click)': '$event.stopPropagation()',
  },
})
export class ColumnResizeDirective implements OnDestroy {
  @Input() width = 120;
  @Input() minWidth = 80;
  @Input() maxWidth = 1200;
  @Input() columnLabel = '';
  @Output() widthChange = new EventEmitter<number>();
  @Output() resizeEnd = new EventEmitter<void>();

  isResizing = false;
  private pointerId: number | null = null;
  private startX = 0;
  private startWidth = 0;

  constructor(private readonly elementRef: ElementRef<HTMLElement>) {}

  onPointerDown(event: PointerEvent): void {
    if (!event.isPrimary || event.button !== 0 || this.pointerId !== null) return;

    event.preventDefault();
    event.stopPropagation();
    const element = this.elementRef.nativeElement;
    element.focus({ preventScroll: true });
    element.setPointerCapture(event.pointerId);
    this.pointerId = event.pointerId;
    this.startX = event.clientX;
    this.startWidth = this.width;
    this.isResizing = true;
  }

  onPointerMove(event: PointerEvent): void {
    if (event.pointerId !== this.pointerId) return;

    event.preventDefault();
    event.stopPropagation();
    this.updateWidth(this.startWidth + event.clientX - this.startX);
  }

  onPointerUp(event: PointerEvent): void {
    if (event.pointerId !== this.pointerId) return;

    event.preventDefault();
    event.stopPropagation();
    this.updateWidth(this.startWidth + event.clientX - this.startX);
    this.finishResize();
  }

  onPointerCancel(event: PointerEvent): void {
    if (event.pointerId !== this.pointerId) return;

    event.stopPropagation();
    this.finishResize();
  }

  onKeyDown(event: KeyboardEvent): void {
    if (this.isResizing) return;

    const step = event.shiftKey ? 50 : 10;
    let nextWidth: number;
    switch (event.key) {
      case 'ArrowLeft':
        nextWidth = this.width - step;
        break;
      case 'ArrowRight':
        nextWidth = this.width + step;
        break;
      case 'Home':
        nextWidth = this.minWidth;
        break;
      case 'End':
        nextWidth = this.maxWidth;
        break;
      default:
        return;
    }

    event.preventDefault();
    event.stopPropagation();
    this.updateWidth(nextWidth);
    this.resizeEnd.emit();
  }

  ngOnDestroy(): void {
    this.releasePointer();
  }

  private updateWidth(value: number): void {
    const nextWidth = Math.max(this.minWidth, Math.min(this.maxWidth, Math.round(value)));
    if (nextWidth === this.width) return;

    this.width = nextWidth;
    this.widthChange.emit(nextWidth);
  }

  private finishResize(): void {
    this.releasePointer();
    this.resizeEnd.emit();
  }

  private releasePointer(): void {
    const pointerId = this.pointerId;
    this.pointerId = null;
    this.isResizing = false;
    const element = this.elementRef.nativeElement;
    if (pointerId !== null && element.hasPointerCapture(pointerId)) {
      element.releasePointerCapture(pointerId);
    }
  }
}
