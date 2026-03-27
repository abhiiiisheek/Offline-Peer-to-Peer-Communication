import warnings
warnings.filterwarnings("ignore")

from transformers import BlenderbotTokenizer, BlenderbotForConditionalGeneration
import sys

tokenizer = BlenderbotTokenizer.from_pretrained("facebook/blenderbot-400M-distill")
model = BlenderbotForConditionalGeneration.from_pretrained("facebook/blenderbot-400M-distill")

input_text = sys.argv[1].lower()

# 🔥 Rule-based fast replies
if "hello" in input_text:
    print("Hi!,Hello!,Hey!")
    exit()

if "how are you" in input_text:
    print("I'm good,Doing well,Fine")
    exit()

inputs = tokenizer([input_text], return_tensors="pt")

reply_ids = model.generate(
    inputs["input_ids"],
    max_new_tokens=20,
    num_beams=5,
    num_return_sequences=3,
    early_stopping=True
)

replies = []

for reply_id in reply_ids:
    reply = tokenizer.decode(reply_id, skip_special_tokens=True).strip()

    if len(reply) > 2:
        reply = reply[:40]
        replies.append(reply)

if len(replies) == 0:
    replies = ["Okay", "Nice", "Got it"]

print(",".join(replies))