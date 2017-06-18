package br.com.gabrielberlanda.spotifyvoicecontroller;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.service.notification.NotificationListenerService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_spotify_voice_controller)
public class SpotifyVoiceController extends AppCompatActivity
{
    //    █████╗ ████████╗████████╗██████╗ ██╗██████╗ ██╗   ██╗████████╗███████╗███████╗
    //   ██╔══██╗╚══██╔══╝╚══██╔══╝██╔══██╗██║██╔══██╗██║   ██║╚══██╔══╝██╔════╝██╔════╝
    //   ███████║   ██║      ██║   ██████╔╝██║██████╔╝██║   ██║   ██║   █████╗  ███████╗
    //   ██╔══██║   ██║      ██║   ██╔══██╗██║██╔══██╗██║   ██║   ██║   ██╔══╝  ╚════██║
    //   ██║  ██║   ██║      ██║   ██║  ██║██║██████╔╝╚██████╔╝   ██║   ███████╗███████║
    //   ╚═╝  ╚═╝   ╚═╝      ╚═╝   ╚═╝  ╚═╝╚═╝╚═════╝  ╚═════╝    ╚═╝   ╚══════╝╚══════╝

    /**
     *
     */
    final static String SPOTIFY_PACKAGE_NAME = "com.spotify.music";

    /**
     * Spotify media controller
     */
    MediaController spotifyController;

    /**
     *
     */
    @ViewById( R.id.playButton )
    Button playButton;

    /**
     *
     */
    @ViewById( R.id.pauseButton )
    Button pauseButton;

    /**
     *
     */
    @ViewById( R.id.nextTrackButton )
    Button nextTrackButton;

    /**
     *
     */
    @ViewById( R.id.previousTrackButton )
    Button previousTrackButton;

    //    ██████╗ ███╗   ██╗    ██╗███╗   ██╗██╗████████╗
    //   ██╔═══██╗████╗  ██║    ██║████╗  ██║██║╚══██╔══╝
    //   ██║   ██║██╔██╗ ██║    ██║██╔██╗ ██║██║   ██║
    //   ██║   ██║██║╚██╗██║    ██║██║╚██╗██║██║   ██║
    //   ╚██████╔╝██║ ╚████║    ██║██║ ╚████║██║   ██║
    //    ╚═════╝ ╚═╝  ╚═══╝    ╚═╝╚═╝  ╚═══╝╚═╝   ╚═╝

    /**
     * On Init da Activity
     */
    @AfterViews
    public void onAfterViews()
    {
        this.checkAndSetSpotifyMediaController();
    }

    //   ██████╗ ███████╗██╗  ██╗ █████╗ ██╗   ██╗██╗ ██████╗ ██████╗ ███████╗
    //   ██╔══██╗██╔════╝██║  ██║██╔══██╗██║   ██║██║██╔═══██╗██╔══██╗██╔════╝
    //   ██████╔╝█████╗  ███████║███████║██║   ██║██║██║   ██║██████╔╝███████╗
    //   ██╔══██╗██╔══╝  ██╔══██║██╔══██║╚██╗ ██╔╝██║██║   ██║██╔══██╗╚════██║
    //   ██████╔╝███████╗██║  ██║██║  ██║ ╚████╔╝ ██║╚██████╔╝██║  ██║███████║
    //   ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝

    /**
     * Método que verifica se possui algum media controler do spotifiy no dispositivo.
     */
    public void checkAndSetSpotifyMediaController()
    {
        final MediaSessionManager mediaSessionManager =
                (MediaSessionManager) this.getSystemService( Context.MEDIA_SESSION_SERVICE);
        final List<MediaController> mediaControllers = mediaSessionManager.getActiveSessions( new ComponentName(this, NotificationListener.class) );

        for ( MediaController mediaController: mediaControllers )
        {
            if( mediaController.getPackageName().equals( SpotifyVoiceController.SPOTIFY_PACKAGE_NAME ) )
            {
                this.spotifyController = mediaController;
                Toast.makeText(this, "Media controller do spotify encontrado.", Toast.LENGTH_SHORT).show();
                break;
            }

            if ( this.spotifyController == null )
            {
                this.finish();
            }
        }
    }

    /**
     * Handler do evento de click no botão de tocar
     */
    @Click( R.id.playButton )
    public void onPlayButtonClick()
    {
        this.spotifyController.getTransportControls().play();
    }

    /**
     *  Handler do evento de click no botão de pausar
     */
    @Click( R.id.pauseButton )
    public void onPauseButtonClick()
    {
        this.spotifyController.getTransportControls().pause();
    }

    /**
     * Handler do evento de click no botão de próxima música
     */
    @Click( R.id.nextTrackButton )
    public void onNextTrackButtonClick()
    {
        this.spotifyController.getTransportControls().skipToNext();
    }

    /**
     * Handler do evento de click no botão de voltar música
     */
    @Click( R.id.previousTrackButton )
    public void onPreviousTrackButtonClick()
    {
        this.spotifyController.getTransportControls().skipToPrevious();
    }
}

