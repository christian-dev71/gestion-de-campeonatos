package com.example.gestiondecampeonatos.ui.screens.teams

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gestiondecampeonatos.R
import com.example.gestiondecampeonatos.data.repository.TeamRepository
import com.example.gestiondecampeonatos.ui.components.TeamAvatar

private val FormBlue = Color(0xFF3264F5)
private val FormInk = Color(0xFF111936)
private val FormMuted = Color(0xFF7E849B)
private val FormBorder = Color(0xFFD9DCE8)

// Composables, Interfaces y Formularios: presenta los campos y emite acciones.
// Recibe el estado del ViewModel; la validación definitiva se realiza en el repositorio.
@Composable
internal fun AddTeamDialog(
    name: String,
    imageUri: String?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    isSaving: Boolean,
    error: TeamFormError?,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val pickLabel = stringResource(if (imageUri == null) R.string.choose_team_image else R.string.change_team_image)
    Dialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.imePadding().padding(horizontal = 20.dp, vertical = 24.dp)
                .widthIn(max = 440.dp).fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            contentColor = FormInk,
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.add_team),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSaving,
                        modifier = Modifier.background(Color(0xFFF0F1F6), CircleShape),
                    ) {
                        Icon(painterResource(R.drawable.ic_close), stringResource(R.string.close_dialog), tint = FormMuted)
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(stringResource(R.string.team_image_optional), color = FormMuted, style = MaterialTheme.typography.bodyMedium)
                    Box(
                        modifier = Modifier.size(128.dp).clip(CircleShape)
                            .background(Color(0xFFEEF3FF))
                            .drawBehind {
                                drawCircle(
                                    color = Color(0xFF9EB8FF),
                                    radius = size.minDimension / 2 - 1.dp.toPx(),
                                    style = Stroke(1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))),
                                )
                            }
                            .clickable(enabled = !isSaving, role = Role.Button, onClickLabel = pickLabel, onClick = onPickImage),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (imageUri == null) {
                            Icon(painterResource(R.drawable.ic_add_photo), pickLabel, modifier = Modifier.size(60.dp), tint = FormBlue)
                        } else {
                            TeamAvatar(imageUri, Modifier.size(120.dp), placeholderRes = R.drawable.ic_add_photo)
                        }
                    }
                    FilledTonalButton(
                        onClick = onPickImage,
                        enabled = !isSaving,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFEBF0FF), contentColor = FormBlue),
                    ) { Text(pickLabel, fontWeight = FontWeight.SemiBold) }
                    if (imageUri != null) {
                        TextButton(onClick = onRemoveImage, enabled = !isSaving) {
                            Text(stringResource(R.string.remove_team_image), color = FormMuted)
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.team_name), fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= TeamRepository.MAX_NAME_LENGTH) onNameChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving,
                        placeholder = { Text(stringResource(R.string.team_name)) },
                        leadingIcon = { Icon(painterResource(R.drawable.ic_teams), null) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        isError = error != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = FormInk, unfocusedTextColor = FormInk,
                            focusedContainerColor = Color(0xFFF8F9FC), unfocusedContainerColor = Color(0xFFF8F9FC),
                            focusedBorderColor = FormBlue, unfocusedBorderColor = FormBorder,
                            focusedLeadingIconColor = FormBlue, unfocusedLeadingIconColor = FormMuted,
                            focusedPlaceholderColor = FormMuted, unfocusedPlaceholderColor = FormMuted,
                            cursorColor = FormBlue,
                        ),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { if (!isSaving) onSave() }),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            stringResource(when (error) {
                                TeamFormError.INVALID_NAME -> R.string.team_name_invalid
                                TeamFormError.DUPLICATE -> R.string.team_duplicate
                                TeamFormError.SAVE_FAILED -> R.string.team_save_error
                                null -> R.string.team_name_hint
                            }),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (error == null) FormMuted else Color(0xFFB3261E),
                        )
                        Text(stringResource(R.string.team_name_counter, name.length, TeamRepository.MAX_NAME_LENGTH), color = FormMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss, enabled = !isSaving,
                        modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, FormBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF405AA4)),
                    ) { Text(stringResource(R.string.cancel), fontWeight = FontWeight.SemiBold) }
                    Button(
                        onClick = onSave, enabled = !isSaving,
                        modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FormBlue, contentColor = Color.White),
                    ) { Text(stringResource(if (isSaving) R.string.saving_team else R.string.save_team), fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}
