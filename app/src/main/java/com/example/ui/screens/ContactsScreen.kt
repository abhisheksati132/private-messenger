package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContactEntity
import com.example.data.remote.CloudUser
import com.example.ui.components.AvatarView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    contacts: List<ContactEntity>,
    cloudUsers: List<CloudUser> = emptyList(),
    myHandle: String,
    presenceMap: Map<String, Boolean> = emptyMap(),
    blockedHandles: Set<String> = emptySet(),
    onStartChatWithContact: (ContactEntity) -> Unit,
    onStartChatWithCloudUser: (CloudUser) -> Unit = {},
    onStartCallWithUser: (name: String, handle: String, isVideo: Boolean) -> Unit,
    onAddCustomContact: (name: String, handle: String) -> Unit,
    onSearchCloudPeer: (handle: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onBlockUser: (String) -> Unit = {},
    onUnblockUser: (String) -> Unit = {},
    onDeleteContact: (String) -> Unit = {},
    isSearchingCloud: Boolean = false,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newHandle by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var lookupMessage by remember { mutableStateOf<String?>(null) }

    // Privacy First: Only display saved contacts
    val filteredContacts = remember(contacts, searchQuery, myHandle) {
        val cleanMe = myHandle.lowercase().replace("@", "").trim()
        contacts
            .filter { it.handle.lowercase() != cleanMe }
            .filter {
                if (searchQuery.isBlank()) true
                else it.name.contains(searchQuery, ignoreCase = true) ||
                        it.handle.contains(searchQuery, ignoreCase = true)
            }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_contacts"),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Contacts",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (filteredContacts.size == 1) "1 contact" else "${filteredContacts.size} contacts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("btn_add_contact_top")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Contact",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .testTag("fab_add_contact")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search saved contacts...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("input_search_contacts")
            )

            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = if (searchQuery.isNotBlank()) "No contacts match \"$searchQuery\"" else "Find your friends",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (searchQuery.isNotBlank()) "Try searching with a different name or handle." else "Add someone by their @handle to message them securely.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Contact")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredContacts, key = { "local_${it.handle}" }) { contact ->
                        val isOnline = presenceMap[contact.handle] ?: false
                        val isBlocked = contact.isBlocked || blockedHandles.contains(contact.handle.lowercase())
                        LocalContactCard(
                            contact = contact,
                            isOnline = isOnline,
                            isBlocked = isBlocked,
                            onChatClick = { onStartChatWithContact(contact) },
                            onVoiceCallClick = { onStartCallWithUser(contact.name, contact.handle, false) },
                            onVideoCallClick = { onStartCallWithUser(contact.name, contact.handle, true) },
                            onBlockClick = { onBlockUser(contact.handle) },
                            onUnblockClick = { onUnblockUser(contact.handle) },
                            onDeleteContact = { onDeleteContact(contact.handle) }
                        )
                    }
                }
            }
        }
    }

    // Clean Add Contact Dialog (Privacy Preserving)
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                lookupMessage = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text("Add Contact", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your friend's exact username or handle to connect privately.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Display Name") },
                        placeholder = { Text("e.g. Alex") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newHandle,
                        onValueChange = { newHandle = it.replace("@", "").trim().lowercase() },
                        label = { Text("Username / Handle") },
                        placeholder = { Text("e.g. alex") },
                        prefix = { Text("@") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    lookupMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanHandle = newHandle.trim()
                        val cleanName = newName.ifBlank { "@$cleanHandle" }.trim()
                        if (cleanHandle.isNotBlank()) {
                            onSearchCloudPeer(cleanHandle) { success, msg ->
                                lookupMessage = msg
                                if (success) {
                                    showAddDialog = false
                                    newHandle = ""
                                    newName = ""
                                    lookupMessage = null
                                }
                            }
                        }
                    },
                    enabled = newHandle.isNotBlank() && !isSearchingCloud,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSearchingCloud) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Add Contact")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        lookupMessage = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LocalContactCard(
    contact: ContactEntity,
    isOnline: Boolean = false,
    isBlocked: Boolean = false,
    onChatClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onBlockClick: () -> Unit = {},
    onUnblockClick: () -> Unit = {},
    onDeleteContact: () -> Unit = {}
) {
    var showBlockMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { if (!isBlocked) onChatClick() }
            .testTag("card_contact_${contact.handle}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isBlocked) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AvatarView(
                name = contact.name,
                bgHex = if (isBlocked) "#B00020" else contact.avatarBgHex,
                textHex = if (isBlocked) "#FFFFFF" else contact.avatarTextHex,
                size = 44.dp,
                imageUrl = contact.avatarUrl,
                isOnline = isOnline && !isBlocked
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isBlocked) "${contact.name} (Blocked)" else contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${contact.handle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!isBlocked) {
                    IconButton(onClick = onVoiceCallClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onVideoCallClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box {
                    IconButton(onClick = { showBlockMenu = true }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More contact options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showBlockMenu,
                        onDismissRequest = { showBlockMenu = false }
                    ) {
                        if (isBlocked) {
                            DropdownMenuItem(
                                text = { Text("Unblock Contact") },
                                onClick = {
                                    showBlockMenu = false
                                    onUnblockClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Block Contact", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showBlockMenu = false
                                    onBlockClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Delete Contact", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showBlockMenu = false
                                onDeleteContact()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        )
                    }
                }
            }
        }
    }
}
