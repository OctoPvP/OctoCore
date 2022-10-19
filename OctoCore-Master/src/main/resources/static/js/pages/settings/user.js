FilePond.registerPlugin(
    // encodes the file as base64 data
    FilePondPluginFileEncode,

    // validates files based on input type
    FilePondPluginFileValidateType,

    // corrects mobile image orientation
    FilePondPluginImageExifOrientation,

    // previews the image
    FilePondPluginImagePreview
);

var filePond = FilePond.create(
    document.querySelector('input'),
    {
        labelIdle: `<div style="color: black;">Drag & Drop your picture or <span class="filepond--label-action" style="color: black !important;">Browse</span></div>`,
        imagePreviewHeight: 170,
        imageCropAspectRatio: '1:1',
        imageResizeTargetWidth: 200,
        imageResizeTargetHeight: 200,
        stylePanelLayout: 'compact circle',
        styleLoadIndicatorPosition: 'center bottom',
        styleButtonRemoveItemPosition: 'center bottom',
    }
);
function upload() {
    const base64 = filePond.getFile().getFileEncodeBase64String();
    console.log({base64});
    if (base64 === undefined || base64 === null) {
        return;
    }
    //send a post to /settings/profile/set-image
    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/api/authenticated/profile/set-image', false);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        base64: base64
    }));
    setTimeout(()=> {
        window.location.reload();
    }, 1000)
}
function clearPicture() {
    console.log('Clearing profile picture');
    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/api/authenticated/profile/set-image', false);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        base64: null
    }));
    setTimeout(()=> {
        window.location.reload();
    }, 1000)
}
