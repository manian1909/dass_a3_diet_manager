import json

def convert_json_to_basic_foods(json_file, output_file):
    """
    Convert food data from JSON format to YADA basic_foods.txt format
    
    JSON format: 
        {"id": "food001", "name": "White Bread Slice", 
         "keywords": ["bread", "sandwich bread", "loaf"], 
         "calories_per_serving": 70}
         
    YADA format:
        Name|keyword1,keyword2,keyword3|calories
    """
    try:
        # Read the JSON data
        with open(json_file, 'r') as f:
            food_data = json.load(f)
        
        # Open the output file for appending
        with open(output_file, 'a') as f:
            # Process each food item
            if isinstance(food_data, list):
                # If JSON is an array of foods
                for food in food_data:
                    process_food_item(food, f)
            else:
                # If JSON is a single food object
                process_food_item(food_data, f)
                
        print(f"Successfully added food data to {output_file}")
        
    except FileNotFoundError:
        print(f"Error: File '{json_file}' not found.")
    except json.JSONDecodeError:
        print(f"Error: '{json_file}' is not a valid JSON file.")
    except Exception as e:
        print(f"Error: {e}")

def process_food_item(food, output_file):
    """Process a single food item and write to output file"""
    # Use name or id as the identifier
    identifier = food.get("name", food.get("id", "Unknown"))
    
    # Join keywords with commas
    keywords = ",".join(food.get("keywords", []))
    
    # Get calories, default to 0 if not found
    calories = food.get("calories_per_serving", 0)
    
    # Write in YADA format: Name|keyword1,keyword2,keyword3|calories
    output_file.write(f"{identifier}|{keywords}|{calories:.2f}\n")

if __name__ == "__main__":
    # Change these paths to match your files
    json_file_path = "food_data.json"
    basic_foods_file = "basic_foods.txt"
    
    convert_json_to_basic_foods(json_file_path, basic_foods_file)