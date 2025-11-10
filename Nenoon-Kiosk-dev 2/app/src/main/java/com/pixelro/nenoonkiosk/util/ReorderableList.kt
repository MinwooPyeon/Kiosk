package com.pixelro.nenoonkiosk.util

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Collections.swap

private const val TAG = "ReorderableList"

/**
 * Reorderable List를 위한 State 클래스
 * 드래그 앤 드롭으로 아이템 순서를 변경할 수 있도록 상태를 관리합니다.
 */
class ReorderableState<T>(
    val lazyListState: LazyListState,
    private val list: SnapshotStateList<T>,
    private val getKey: (T) -> Any,
    private val onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    private val scope: CoroutineScope
) {
    // 드래그 중인 아이템의 key
    var draggedItemKey by mutableStateOf<Any?>(null)
        private set

    // 드래그 중인 아이템의 인덱스 (key로부터 실시간 계산)
    val draggedItemIndex: Int
        get() = draggedItemKey?.let { key ->
            list.indexOfFirst { getKey(it) == key }
        } ?: -1

    // 드래그 중인 아이템의 초기 Y 위치
    private var initialYBounds by mutableFloatStateOf(0f)

    // 드래그 거리
    var distance by mutableFloatStateOf(0f)
        private set

    // 자동 스크롤 Job
    private var scrollJob: Job? = null

    /**
     * 드래그 시작 (key로 시작 - 현재 index는 key로부터 동적으로 조회)
     */
    fun onDragStart(key: Any) {
        // key로부터 현재 index 찾기
        val currentIndex = list.indexOfFirst { getKey(it) == key }
        if (currentIndex == -1) return

        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                item.index == currentIndex
            }?.also { item ->
                draggedItemKey = key
                initialYBounds = item.offset.toFloat()
            }
    }

    /**
     * 드래그 중
     */
    fun onDrag(offset: Offset) {
        if (draggedItemIndex == -1) return

        distance += offset.y

        // 현재 드래그된 아이템의 중심 위치 계산
        val draggedItem = lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == draggedItemIndex } ?: return

        // 스크롤이 발생했다면 initialYBounds와 distance 조정
        val currentOffset = draggedItem.offset.toFloat()
        if (currentOffset != initialYBounds) {
            val scrollDelta = currentOffset - initialYBounds
            distance -= scrollDelta
            initialYBounds = currentOffset
       }

        val draggedItemCenter = initialYBounds + distance + draggedItem.size / 2f

        // threshold를 넘었는지 확인하고 순서 변경
        val targetItem = lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                item.index != draggedItemIndex &&
                draggedItemCenter in (item.offset.toFloat())..(item.offset.toFloat() + item.size)
            }

        targetItem?.let {
            // 아이템의 중심점을 기준으로 교체 여부 결정
            val targetCenter = it.offset + it.size / 2f
            val shouldSwap = if (it.index < draggedItemIndex) {
                // 위로 드래그: 타겟 아이템의 중심을 넘어야 함
                draggedItemCenter < targetCenter
            } else {
                // 아래로 드래그: 타겟 아이템의 중심을 넘어야 함
                draggedItemCenter > targetCenter
            }

            if (shouldSwap) {
                val targetIndex = it.index
                val targetItemOffset = it.offset.toFloat()

                // 현재 스크롤 위치 저장
                val savedScrollIndex = lazyListState.firstVisibleItemIndex
                val savedScrollOffset = lazyListState.firstVisibleItemScrollOffset

                // IMPORTANT: swap 전에 draggedItemIndex를 저장!
                val fromIndex = draggedItemIndex

                list.reorder(fromIndex, targetIndex)
                onMove(fromIndex, targetIndex)

                // 스크롤 위치 복원 (swap 후 composition에서 발생하는 스크롤 점프 방지)
                scope.launch {
                    lazyListState.scrollToItem(savedScrollIndex, savedScrollOffset)
                }

                // swap 후 draggedItem의 새 offset은 targetItem의 이전 offset과 동일
                // (인접한 아이템끼리 swap하므로)
                val offsetDelta = targetItemOffset - initialYBounds
                distance -= offsetDelta

                initialYBounds = targetItemOffset

            }
        }

        // 자동 스크롤 처리
        handleAutoScroll()
    }

    /**
     * 드래그 종료
     */
    fun onDragEnd() {
        draggedItemKey = null
        distance = 0f
        initialYBounds = 0f
        scrollJob?.cancel()
        scrollJob = null
    }

    /**
     * 드래그 취소
     */
    fun onDragCancel() {
        onDragEnd()
    }

    /**
     * 자동 스크롤 처리
     */
    private fun handleAutoScroll() {
        val draggedItem = lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == draggedItemIndex } ?: return

        // 드래그 중인 아이템의 실제 화면상 위치 사용 (translationY 반영)
        val startOffset = draggedItem.offset + distance
        val endOffset = startOffset + draggedItem.size

        val viewportStartOffset = lazyListState.layoutInfo.viewportStartOffset
        val viewportEndOffset = lazyListState.layoutInfo.viewportEndOffset

        val scrollThreshold = 8f // 스크롤 트리거 영역 (넓게 설정)
        val scrollSpeed = 18f // 스크롤 속도 (픽셀/프레임)

        when {
            // 위로 자동 스크롤
            startOffset < viewportStartOffset + scrollThreshold &&
            (lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0) -> {
                if (scrollJob?.isActive != true) {
                    scrollJob = scope.launch {
                        while (draggedItemIndex != -1) {
                            val currentItem = lazyListState.layoutInfo.visibleItemsInfo
                                .firstOrNull { it.index == draggedItemIndex } ?: break
                            val currentStart = currentItem.offset + distance

                            // 맨 위에 도달했는지 확인
                            if (currentStart >= viewportStartOffset + scrollThreshold ||
                                (lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0)) {
                                break
                            }

                            lazyListState.scrollBy(-scrollSpeed)
                            kotlinx.coroutines.delay(16) // ~60fps
                        }
                    }
                }
            }
            // 아래로 자동 스크롤
            endOffset > viewportEndOffset - scrollThreshold -> {
                if (scrollJob?.isActive != true) {
                    scrollJob = scope.launch {
                        while (draggedItemIndex != -1) {
                            val currentItem = lazyListState.layoutInfo.visibleItemsInfo
                                .firstOrNull { it.index == draggedItemIndex } ?: break
                            val currentEnd = currentItem.offset + distance + currentItem.size

                            // 리스트 끝에 도달했는지 확인
                            val isAtEnd = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == list.size - 1 &&
                                         lazyListState.canScrollForward.not()

                            if (currentEnd <= viewportEndOffset - scrollThreshold || isAtEnd) {
                                break
                            }

                            lazyListState.scrollBy(scrollSpeed)
                            kotlinx.coroutines.delay(16) // ~60fps
                        }
                    }
                }
            }
            else -> {
                scrollJob?.cancel()
                scrollJob = null
            }
        }
    }

    /**
     * 특정 인덱스 아이템의 그래픽 레이어 수정자 반환
     */
    fun getItemModifier(index: Int): Modifier {
        return if (index == draggedItemIndex) {
            Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationY = distance
                }
        } else {
            Modifier
        }
    }
}

/**
 * ReorderableState를 생성하고 기억하는 Composable 함수
 */
@Composable
fun <T> rememberReorderableState(
    list: SnapshotStateList<T>,
    getKey: (T) -> Any,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
    lazyListState: LazyListState = rememberLazyListState()
): ReorderableState<T> {
    val scope = rememberCoroutineScope()
    return remember(list, lazyListState) {
        ReorderableState(lazyListState, list, getKey, onMove, scope)
    }
}

/**
 * Modifier 확장 함수: 드래그 핸들에 적용할 제스처
 */
fun Modifier.dragHandle(
    state: ReorderableState<*>,
    key: Any,
    onUpdateList: () -> Unit = {},
    onDragEnd: () -> Unit = {}
): Modifier {
    return this.pointerInput(key) {
        detectDragGestures(
            onDragStart = { _ ->
                state.onDragStart(key)
            },
            onDrag = { change, dragAmount ->
                change.consume()
                state.onDrag(Offset(0f, dragAmount.y))
                onUpdateList()
            },
            onDragEnd = {
                state.onDragEnd()
                onUpdateList()
                onDragEnd()  // DB 저장
            },
            onDragCancel = {
                state.onDragCancel()
                onUpdateList()
            }
        )
    }
}

/**
 * SnapshotStateList 확장 함수: 아이템 순서 변경
 */
private fun <T> SnapshotStateList<T>.reorder(from: Int, to: Int) {
    if (from < 0 || from >= size || to < 0 || to >= size || from == to) {
        return
    }

    // 인접한 아이템끼리 연속으로 swap
    if (from < to) {
        // 아래로 이동
        for (i in from until to) {
            swap(this, i, i + 1)
        }
    } else {
        // 위로 이동
        for (i in from downTo to + 1) {
            swap(this, i, i - 1)
        }
    }
}