package com.mistplay.carddeckgame.feature.deck.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.mistplay.carddeckgame.feature.deck.domain.Card
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    viewModel: DeckViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Card Deck") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            DeckAreaSection(viewModel, snackbarHostState)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Drawn cards",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            DrawnCardsListSection(viewModel)
        }
    }
    SelectedCardDialogSection(viewModel)
}

/**
 * Collects only [DeckViewModel.deckUiState]. Recomposes when deck/loading/error change, not when cards or selection change.
 */
@Composable
private fun DeckAreaSection(
    viewModel: DeckViewModel,
    snackbarHostState: SnackbarHostState
) {
    val deckState by viewModel.deckUiState.collectAsState()
    LaunchedEffect(deckState.errorMessage) {
        deckState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, withDismissAction = true)
            viewModel.handle(DeckIntent.Retry)
        }
    }
    DeckArea(
        hasDeck = deckState.deckId != null,
        remaining = deckState.remaining,
        isLoading = deckState.isLoading,
        onShuffle = { viewModel.handle(DeckIntent.ShuffleDeck) },
        onReshuffle = { viewModel.handle(DeckIntent.ReshuffleDeck) },
        onDraw = { viewModel.handle(DeckIntent.DrawCards(1)) }
    )
}

/**
 * Collects only [DeckViewModel.drawnCards]. Recomposes only when the drawn cards list changes.
 */
@Composable
private fun DrawnCardsListSection(viewModel: DeckViewModel) {
    val cards by viewModel.drawnCards.collectAsState()
    DrawnCardsList(
        cards = cards,
        onCardClick = { viewModel.handle(DeckIntent.CardClicked(it)) }
    )
}

/**
 * Collects only [DeckViewModel.selectedCard]. Recomposes only when selection changes.
 */
@Composable
private fun SelectedCardDialogSection(viewModel: DeckViewModel) {
    val selectedCard by viewModel.selectedCard.collectAsState()
    selectedCard?.let { card ->
        CardDetailDialog(
            card = card,
            onDismiss = { viewModel.handle(DeckIntent.DismissCardDetail) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreenContent(
    state: DeckState,
    onIntent: (DeckIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Card Deck") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            DeckArea(
                hasDeck = state.deckId != null,
                remaining = state.remaining,
                isLoading = state.isLoading,
                onShuffle = { onIntent(DeckIntent.ShuffleDeck) },
                onReshuffle = { onIntent(DeckIntent.ReshuffleDeck) },
                onDraw = { onIntent(DeckIntent.DrawCards(1)) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Drawn cards",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            DrawnCardsList(
                cards = state.drawnCards,
                onCardClick = { onIntent(DeckIntent.CardClicked(it)) }
            )
        }
    }
    state.selectedCard?.let { card ->
        CardDetailDialog(
            card = card,
            onDismiss = { onIntent(DeckIntent.DismissCardDetail) }
        )
    }
}

@Composable
private fun DeckArea(
    hasDeck: Boolean,
    remaining: Int,
    isLoading: Boolean,
    onShuffle: () -> Unit,
    onReshuffle: () -> Unit,
    onDraw: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                DeckBackImage(hasDeck = hasDeck, onClick = onDraw)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (hasDeck) {
                Text(
                    "Remaining: $remaining",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onShuffle,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text(if (hasDeck) "New deck" else "Shuffle deck")
                }
                if (hasDeck) {
                    OutlinedButton(
                        onClick = onReshuffle,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Reshuffle")
                    }
                    Button(
                        onClick = onDraw,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && remaining > 0
                    ) {
                        Text("Draw")
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckBackImage(hasDeck: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (hasDeck) 1f else 0.9f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "deckScale"
    )
    Box(
        modifier = Modifier
            .size(120.dp, 168.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = hasDeck, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = DeckConstants.BACK_OF_CARD_IMAGE_URL,
            contentDescription = "Deck of cards",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f / 1.4f),
            contentScale = ContentScale.Fit
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawnCardsList(
    cards: List<Card>,
    onCardClick: (Card) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = cards,
            key = { index, _ -> index }
        ) { index, card ->
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "card"
            )
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                CardItem(
                    card = card,
                    modifier = Modifier.scale(scale),
                    onClick = { onCardClick(card) }
                )
            }
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f / 1.4f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = card.imageUrl,
            contentDescription = "${card.value} of ${card.suit}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun CardDetailDialog(card: Card, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1.4f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Text(
                    "${card.value} of ${card.suit}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

// ——— Preview helpers ———

private val previewSampleCards = listOf(
    Card("AS", "https://deckofcardsapi.com/static/img/AS.png", "ACE", "SPADES"),
    Card("KH", "https://deckofcardsapi.com/static/img/KH.png", "KING", "HEARTS"),
    Card("QD", "https://deckofcardsapi.com/static/img/QD.png", "QUEEN", "DIAMONDS"),
)

private val previewSampleCard = previewSampleCards.first()

@Preview(name = "Deck screen – initial", showBackground = true)
@Composable
private fun DeckScreenPreviewInitial() {
    MaterialTheme {
        DeckScreenContent(
            state = DeckState(),
            onIntent = {}
        )
    }
}

@Preview(name = "Deck screen – with deck", showBackground = true)
@Composable
private fun DeckScreenPreviewWithDeck() {
    MaterialTheme {
        DeckScreenContent(
            state = DeckState(deckId = "preview", remaining = 52, drawnCards = emptyList()),
            onIntent = {}
        )
    }
}

@Preview(name = "Deck screen – with drawn cards", showBackground = true)
@Composable
private fun DeckScreenPreviewWithDrawnCards() {
    MaterialTheme {
        DeckScreenContent(
            state = DeckState(
                deckId = "preview",
                remaining = 49,
                drawnCards = previewSampleCards
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Deck area – no deck", showBackground = true)
@Composable
private fun DeckAreaPreviewNoDeck() {
    MaterialTheme {
        DeckArea(
            hasDeck = false,
            remaining = 0,
            isLoading = false,
            onShuffle = {},
            onReshuffle = {},
            onDraw = {}
        )
    }
}

@Preview(name = "Deck area – loading", showBackground = true)
@Composable
private fun DeckAreaPreviewLoading() {
    MaterialTheme {
        DeckArea(
            hasDeck = false,
            remaining = 0,
            isLoading = true,
            onShuffle = {},
            onReshuffle = {},
            onDraw = {}
        )
    }
}

@Preview(name = "Deck area – with deck", showBackground = true)
@Composable
private fun DeckAreaPreviewWithDeck() {
    MaterialTheme {
        DeckArea(
            hasDeck = true,
            remaining = 52,
            isLoading = false,
            onShuffle = {},
            onReshuffle = {},
            onDraw = {}
        )
    }
}

@Preview(name = "Card item", showBackground = true)
@Composable
private fun CardItemPreview() {
    MaterialTheme {
        CardItem(
            card = previewSampleCard,
            modifier = Modifier.size(width = 80.dp, height = 112.dp),
            onClick = {}
        )
    }
}

@Preview(name = "Card detail dialog", showBackground = true)
@Composable
private fun CardDetailDialogPreview() {
    MaterialTheme {
        CardDetailDialog(card = previewSampleCard, onDismiss = {})
    }
}
