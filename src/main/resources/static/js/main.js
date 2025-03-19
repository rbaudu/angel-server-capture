/**
 * Script principal pour Angel Server Capture
 */

// Fonction auto-exécutante pour encapsuler le code et éviter les variables globales
(function() {
    'use strict';

    // Objet principal de l'application
    const AngelApp = {
        // Initialisation de l'application
        init: function() {
            this.setupApiClient();
            this.setupEventListeners();
            this.checkServerStatus();
        },

        // Configuration du client API pour les appels REST
        setupApiClient: function() {
            this.apiClient = {
                // Récupérer le statut du serveur
                getStatus: function() {
                    return fetch('/angel/api/capture/status')
                        .then(response => response.json())
                        .catch(error => {
                            console.error('Erreur lors de la récupération du statut :', error);
                            return { error: true, message: 'Impossible de se connecter au serveur' };
                        });
                },
                
                // Récupérer la configuration
                getConfig: function() {
                    return fetch('/angel/api/capture/config')
                        .then(response => response.json())
                        .catch(error => {
                            console.error('Erreur lors de la récupération de la configuration :', error);
                            return { error: true, message: 'Impossible de récupérer la configuration' };
                        });
                },
                
                // Démarrer la capture
                startCapture: function() {
                    return fetch('/angel/api/capture/start', { method: 'POST' })
                        .then(response => response.json())
                        .catch(error => {
                            console.error('Erreur lors du démarrage de la capture :', error);
                            return { success: false, message: 'Impossible de démarrer la capture' };
                        });
                },
                
                // Arrêter la capture
                stopCapture: function() {
                    return fetch('/angel/api/capture/stop', { method: 'POST' })
                        .then(response => response.json())
                        .catch(error => {
                            console.error('Erreur lors de l\'arrêt de la capture :', error);
                            return { success: false, message: 'Impossible d\'arrêter la capture' };
                        });
                },
                
                // Redémarrer la capture
                restartCapture: function() {
                    return fetch('/angel/api/capture/restart', { method: 'POST' })
                        .then(response => response.json())
                        .catch(error => {
                            console.error('Erreur lors du redémarrage de la capture :', error);
                            return { success: false, message: 'Impossible de redémarrer la capture' };
                        });
                }
            };
        },

        // Configuration des écouteurs d'événements
        setupEventListeners: function() {
            // Détection de la page actuelle
            const currentPath = window.location.pathname;
            
            // Boutons communs à toutes les pages
            const commonButtons = document.querySelectorAll('[id$="CaptureButton"]');
            if (commonButtons.length > 0) {
                commonButtons.forEach(button => {
                    button.addEventListener('click', (event) => {
                        const action = event.target.id.replace('CaptureButton', '').toLowerCase();
                        this.handleCaptureAction(action);
                    });
                });
            }
            
            // Écouteurs spécifiques à la page d'accueil
            if (currentPath === '/' || currentPath === '/angel/') {
                // Déjà géré par le script inclus directement dans la page
            }
            
            // Écouteurs spécifiques à la page de visualisation en direct
            if (currentPath === '/live' || currentPath === '/angel/live') {
                // Déjà géré par le script inclus directement dans la page
            }
            
            // Écouteurs spécifiques à la page de configuration
            if (currentPath === '/config' || currentPath === '/angel/config') {
                const configForm = document.getElementById('configForm');
                if (configForm) {
                    configForm.addEventListener('submit', (event) => {
                        event.preventDefault();
                        this.saveConfiguration(new FormData(configForm));
                    });
                }
            }
        },

        // Vérifier l'état du serveur
        checkServerStatus: function() {
            this.apiClient.getStatus()
                .then(status => {
                    if (status.error) {
                        this.showAlert('danger', 'Erreur de connexion au serveur. Veuillez vérifier que le serveur est en cours d\'exécution.');
                        return;
                    }
                    
                    // Mettre à jour l'interface en fonction du statut
                    this.updateStatusUI(status);
                });
        },

        // Mettre à jour l'interface en fonction du statut
        updateStatusUI: function(status) {
            const startButton = document.getElementById('startButton');
            const stopButton = document.getElementById('stopButton');
            const restartButton = document.getElementById('restartButton');
            
            if (startButton && stopButton) {
                startButton.disabled = status.running;
                stopButton.disabled = !status.running;
            }
        },

        // Gérer les actions de capture (démarrer, arrêter, redémarrer)
        handleCaptureAction: function(action) {
            let apiCall;
            let loadingMessage;
            
            switch (action) {
                case 'start':
                    apiCall = this.apiClient.startCapture;
                    loadingMessage = 'Démarrage de la capture...';
                    break;
                case 'stop':
                    apiCall = this.apiClient.stopCapture;
                    loadingMessage = 'Arrêt de la capture...';
                    break;
                case 'restart':
                    apiCall = this.apiClient.restartCapture;
                    loadingMessage = 'Redémarrage de la capture...';
                    break;
                default:
                    console.error('Action non reconnue :', action);
                    return;
            }
            
            // Afficher un message de chargement
            this.showAlert('info', loadingMessage);
            
            // Appeler l'API
            apiCall.call(this.apiClient)
                .then(result => {
                    if (result.success) {
                        this.showAlert('success', result.message);
                        this.updateStatusUI({ running: result.running });
                    } else {
                        this.showAlert('danger', result.message);
                    }
                });
        },

        // Enregistrer la configuration
        saveConfiguration: function(formData) {
            // Convertir FormData en objet JSON
            const config = {};
            for (let [key, value] of formData.entries()) {
                // Convertir les valeurs booléennes
                if (value === 'on') {
                    value = true;
                } else if (value === 'off') {
                    value = false;
                }
                
                // Convertir les valeurs numériques
                if (!isNaN(value) && value !== '') {
                    value = Number(value);
                }
                
                config[key] = value;
            }
            
            // Envoi des données (à implémenter dans une future version)
            console.log('Configuration à enregistrer :', config);
            this.showAlert('warning', 'La sauvegarde de la configuration sera disponible dans une future version.');
        },

        // Afficher une alerte
        showAlert: function(type, message) {
            const alertHtml = `
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            `;
            
            // Ajouter l'alerte au début de la page
            const cardBody = document.querySelector('.card-body');
            if (cardBody) {
                // Créer un div temporaire pour convertir la chaîne HTML en éléments
                const temp = document.createElement('div');
                temp.innerHTML = alertHtml;
                
                // Insérer l'alerte au début du contenu
                cardBody.insertBefore(temp.firstChild, cardBody.firstChild);
                
                // Auto-disparition après 5 secondes pour les alertes non-critiques
                if (type !== 'danger') {
                    setTimeout(function() {
                        const alerts = document.querySelectorAll('.alert');
                        if (alerts.length > 0) {
                            const bootstrap = window.bootstrap;
                            if (bootstrap && bootstrap.Alert) {
                                const bsAlert = new bootstrap.Alert(alerts[0]);
                                bsAlert.close();
                            } else {
                                // Fallback si l'objet bootstrap n'est pas disponible
                                alerts[0].remove();
                            }
                        }
                    }, 5000);
                }
            }
        }
    };

    // Initialiser l'application au chargement du document
    document.addEventListener('DOMContentLoaded', function() {
        AngelApp.init();
    });

})();
