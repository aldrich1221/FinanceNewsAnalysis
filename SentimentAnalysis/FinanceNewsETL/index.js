// dependencies
const AWS = require('aws-sdk');
const util = require('util');


// get reference to S3 client
const s3 = new AWS.S3();

exports.handler = async (event, context, callback) => {

    const date=new Date().toString()
    // Read options from the event parameter.
    console.log("Reading options from event:\n", util.inspect(event, {depth: 5}));
    //const srcBucket = event.Records[0].s3.bucket.name;
    const srcBucket = "aldrichopenstorage";
    
    // Object key may have spaces or unicode non-ASCII characters.
    //const srcKey    = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, " "));
    const srcKey    = "RawNewsFile/rawnews";
    
    const dstBucket = "aldrichopenstorage/refinedNewsFile";
    const dstKey    = "refinedNewsFile-"+date;

 
   

    // Download the image from the S3 source bucket. 

    try {
        const params = {
            Bucket: srcBucket,
            Key: srcKey
        };
        var origimage = await s3.getObject(params).promise();

    } catch (error) {
        console.log(error);
        return;
    }  

    // set thumbnail width. Resize will set the height automatically to maintain aspect ratio.
    const width  = 200;

    // Use the Sharp module to resize the image and save in a buffer.
    try { 
        //var buffer = await sharp(origimage.Body).resize(width).toBuffer();
        var buffer = await origimage.Body.toBuffer();
            
    } catch (error) {
        console.log(error);
        return;
    } 

    // Upload the thumbnail image to the destination bucket
    try {
        const destparams = {
            Bucket: dstBucket,
            Key: dstKey,
            Body: buffer,
            ContentType: "text"
        };

        //const putResult = await s3.putObject(destparams).promise(); 
        
    } catch (error) {
        console.log(error);
        return;
    } 
        
    console.log('Successfully resized ' + srcBucket + '/' + srcKey +
        ' and uploaded to ' + dstBucket + '/' + dstKey); 
};